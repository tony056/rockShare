package com.example.tungying_chao.rockshare;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tungying_chao.beanconnection.BeanConnectionApplication;
import com.example.tungying_chao.utilities.Constant;
import com.example.tungying_chao.utilities.MusicItemAdapter;
import com.example.tungying_chao.utilities.RockShareServerHandler;
import com.example.tungying_chao.utilities.SongInfo;
import com.example.tungying_chao.utilities.SongManager;
import com.gc.materialdesign.views.ButtonFlat;
import com.gc.materialdesign.views.Slider;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.pubnub.api.Callback;
import com.pubnub.api.Pubnub;
import com.pubnub.api.PubnubError;
import com.pubnub.api.PubnubException;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by tungying-chao on 6/3/15.
 */
public class MusicPlayerActivity extends Activity {

    private static final String TAG = "MusicPlayerActivity";
    private MediaPlayer mediaPlayer;
//    private MusicItemAdapter adapter = new MusicItemAdapter(this);
    private List<SongInfo> list = new ArrayList<SongInfo>();
    private boolean isPlaying = true;
    private int songIndex = 0;
    private AudioManager audioManager;
    private RockShareServerHandler rockShareServerHandler;
    private Dialog volumeDialog;
    private Slider volumeSlider;
    private ButtonFlat okButton;
    private ProgressDialog progressDialog;
    Pubnub pubnub;
    private String pubnubChannel = "";

    private TextView songNameTextView;
    private TextView authorNameTextView;
    private ImageView playAndPauseImageView;
    private ImageView nextSongImageView;
    private ImageView prevSongImageView;
    private ImageView volumeImageView;
    private ImageView.OnClickListener mClickListener = new ImageView.OnClickListener(){

        @Override
        public void onClick(View v) {
            int id = v.getId();
            switch (id){
                case R.id.playAndPause:
                    if(isPlaying == false){
                        playSong();
                    }else {
                        stopSong();
                    }
                    isPlaying = !isPlaying;
                    break;
                case R.id.fastForward:
                    nextSong();
                    break;
                case R.id.reWind:
                    prevSong();
                    break;
                case R.id.audioVolume:
                    openDialog();
                    break;
            }

        }
    };

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
//            String event = "";
            if(intent.hasExtra(BeanConnectionApplication.EVENT)){
                int cmd = intent.getIntExtra(BeanConnectionApplication.EVENT, 0);
                Log.d(TAG, "cmd: " + cmd);
                switch (cmd){
                    case 1:
//                        "Single Click";
//                        rockShareServerHandler.updateState(1);
                        stopPlayerAndSave();
                        updateStateOnParse(1);
                        break;
                    case 2:
//                        vibrateNotification(2);
                        checkWhoIsSharing(2);
//                        "Double Click";
                        break;
                    case 3:
//                        vibrateNotification(3);
                        checkWhoIsSharing(3);
//                        "Triple Click";
                        break;
                    case -1:
//                        vibrateNotification(-1);
                        checkWhoIsSharing(-1);
//                        "Long Click";
                        break;
                    default:
                        break;
                }
//                if(event.length() == 0)
//                    return;
//                Toast.makeText(getApplicationContext(), event, Toast.LENGTH_SHORT).show();
            }
            String artist = intent.getStringExtra("artist");
            String album = intent.getStringExtra("album");
            String track = intent.getStringExtra("track");

        }
    };

    Callback subscribeCallback = new Callback() {
        @Override
        public void successCallback(String channel, Object message) {
            Log.d(TAG, ParseUser.getCurrentUser().getUsername() + ", " + channel + ", got message");
//            if(channel.equals(ParseUser.getCurrentUser().getUsername())){
                try {
                    JSONObject object = new JSONObject(message.toString());
                    String status = object.getString("broadcast");
                    switch (status){
                        case "ok":
                            checkProgressStatusAndChange();
//                            playSong();
                            if(!object.getString("from").equals(ParseUser.getCurrentUser().getUsername()))
                                publishMessage("play");
                            break;
                        case "play":
                            if(progressDialog.isShowing())
                                progressDialog.dismiss();
                            playSong();
                            break;
                        case "pause":
                            stopSong();
                            break;

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
//            }
        }

        @Override
        public void connectCallback(String channel, Object message) {
            super.connectCallback(channel, message);
        }

        @Override
        public void errorCallback(String channel, PubnubError error) {
            super.errorCallback(channel, error);
        }
    };


    private void publishMessage(String msg) {
        JSONObject object = new JSONObject();

        try {
            object.put("from", ParseUser.getCurrentUser().getUsername());
            object.put("broadcast", msg);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        pubnub.publish(pubnubChannel, object, publishCallback);
    }

    private void checkProgressStatusAndChange() {
        if(progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
        else {
            try {
                progressDialog.show();
            }catch (Exception e){
                Log.d(TAG, e.getMessage());
            }

        }
    }

    Callback publishCallback = new Callback() {
        @Override
        public void successCallback(String channel, Object message) {
            Log.d(TAG, "pubnub publish good");
        }

        @Override
        public void errorCallback(String channel, PubnubError error) {
            super.errorCallback(channel, error);
        }

        @Override
        public void disconnectCallback(String channel, Object message) {
            super.disconnectCallback(channel, message);
        }
    };

    private void stopPlayerAndSave() {
        stopSong();
        SharedPreferences sharedPreferences = getSharedPreferences(Constant.MEDIA_STATE, 0);
        sharedPreferences.edit().putString(Constant.SONG, list.get(songIndex).getName())
                .putInt(Constant.OFFSET, mediaPlayer.getCurrentPosition()).commit();
    }


    private void openDialog() {
//        mediaPlayer.pause();
        volumeDialog.show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("onCreate", "MusicPlayerActivity");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.music_player_activity);
        rockShareServerHandler = ((BeanConnectionApplication)getApplicationContext()).getRockShareServerHandler();
        playAndPauseImageView = (ImageView)findViewById(R.id.playAndPause);
        nextSongImageView = (ImageView) findViewById(R.id.fastForward);
        prevSongImageView = (ImageView) findViewById(R.id.reWind);
        volumeImageView = (ImageView) findViewById(R.id.audioVolume);
        songNameTextView = (TextView) findViewById(R.id.playerSongName);
        authorNameTextView = (TextView) findViewById(R.id.playerAuthorName);
        playAndPauseImageView.setImageResource(R.drawable.pause);
        playAndPauseImageView.setOnClickListener(mClickListener);
        nextSongImageView.setOnClickListener(mClickListener);
        prevSongImageView.setOnClickListener(mClickListener);
        volumeImageView.setOnClickListener(mClickListener);
        mediaPlayer = new MediaPlayer();
        pubnub = ((BeanConnectionApplication)getApplicationContext()).getMyPubNub();
        initLists();
        songIndex = getSongIndex();
        initMediaPlayer(songIndex);
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        initDialog();
        initProgressDialog();
    }

    private void initProgressDialog() {
        Log.d("initProgressDialog", "initProgressDialog");
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Waiting");
        progressDialog.setMessage("Please wait for sync!");
    }

    private void initLists() {
        Log.d("initList", "initList");
        SongManager songManager = new SongManager(this);
        list = songManager.getSongsList();
    }

    @Override
    public void onPause(){
        super.onPause();
        unregisterReceiver(broadcastReceiver);
        SharedPreferences sharedPreferences = getSharedPreferences(Constant.MEDIA_STATE, 0);
        sharedPreferences.edit().putString(Constant.SONG, list.get(songIndex).getName())
                .putInt(Constant.OFFSET, mediaPlayer.getCurrentPosition()).commit();
        mediaPlayer.pause();
        Log.d(TAG, "onPause");

    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop");
    }

    @Override
    public void onResume(){
        Log.d(TAG, "onResume");
        super.onResume();
        IntentFilter mFilter = new IntentFilter(BeanConnectionApplication.TOUCH_EVENT);
        registerReceiver(broadcastReceiver, mFilter);
        if(mediaPlayer == null){
            mediaPlayer = new MediaPlayer();
        }else {
            readSharePreference();
            if(!mediaPlayer.isPlaying())
                mediaPlayer.start();
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void initMediaPlayer(int index){
        Log.d("initMediaPlayer", "initMediaPlayer");
        mediaPlayer.reset();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        try {
            mediaPlayer.setDataSource(list.get(index).getPath());
            updateInfo();
            mediaPlayer.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                nextSong();
            }
        });
       playSong(songIndex);
    }

    private void playSong(int index) {
        mediaPlayer.start();
        Log.d("mediaplayerstart", "mediaplayerstart");
        rockShareServerHandler.updateSong(list.get(index).getName());
        rockShareServerHandler.updateOffset(mediaPlayer.getCurrentPosition());
        playAndPauseImageView.setImageResource(R.drawable.pause);
        if(pubnubChannel.equals(ParseUser.getCurrentUser().getUsername())){
            publishMessage("play");
        }
    }

    private int getSongIndex(){
        return getIntent().getExtras().getInt("index");
    }

    private void nextSong(){
        mediaPlayer.pause();
        if(songIndex + 1 < list.size())
            songIndex++;
        else
            songIndex = 0;
        initMediaPlayer(songIndex);
        updateInfo();
    }

    private void prevSong(){
        mediaPlayer.pause();
        if(songIndex - 1 >= 0)
            songIndex--;
        else
            songIndex = list.size() - 1;
        initMediaPlayer(songIndex);
        updateInfo();
    }

    private void setVolume(int volume){
        if(audioManager == null){
            Log.d(TAG, "null manager");
            return;
        }
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, volume, 0);
    }

    private void initDialog(){
        Log.d("initDialog", "initDialog");
        if(volumeDialog == null)
            volumeDialog = new Dialog(this);
        volumeDialog.setContentView(R.layout.music_player_volume_dialog);
        int maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        okButton = (ButtonFlat) volumeDialog.findViewById(R.id.volumeOkButton);
        volumeSlider = (Slider) volumeDialog.findViewById(R.id.slider);
        volumeSlider.setMax(maxVolume);
        int midValue = maxVolume / 2;
        volumeSlider.setValue(midValue);
        setVolume(midValue);
        volumeSlider.setOnValueChangedListener(new Slider.OnValueChangedListener() {
            @Override
            public void onValueChanged(int i) {
                setVolume(i);
            }
        });
        okButton.setOnClickListener(new ButtonFlat.OnClickListener() {

            @Override
            public void onClick(View v) {
                volumeDialog.dismiss();
            }
        });
    }

    private void updateInfo(){
        String song = list.get(songIndex).getName();
        String author = list.get(songIndex).getAuthor();
        songNameTextView.setText(song);
        authorNameTextView.setText(author);
    }

    private void readSharePreference(){
        SharedPreferences sharedPreferences = getSharedPreferences(Constant.MEDIA_STATE, 0);
        Log.d(TAG, "read: " + sharedPreferences.getString(Constant.SONG, "Cool") + ", " + sharedPreferences.getInt(Constant.OFFSET, 0));
    }

    private void playSong(){
        if(pubnubChannel.equals(ParseUser.getCurrentUser().getUsername()) && !mediaPlayer.isPlaying()){
            publishMessage("play");
        }
        mediaPlayer.start();
//        rockShareServerHandler.updateSong(list.get(songIndex).getName());
//        rockShareServerHandler.updateOffset(mediaPlayer.getCurrentPosition());

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                playAndPauseImageView.setImageResource(R.drawable.pause);
            }
        });
    }

    private void checkWhoIsSharing(int state){
        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.whereEqualTo(Constant.ACCEPT_STATE, state);
//        query.orderByAscending("")
        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> list, ParseException e) {
                if (e == null) {
                    if (list.size() == 1 && list.get(0) != ParseUser.getCurrentUser()) {
                        Log.d(TAG, "update from parse");
                        pubnubChannel = list.get(0).getUsername();
                        updateInfoByParse(list.get(0).getString(Constant.SONG));
                        updateMusic(list.get(0).getString(Constant.URL), list.get(0).getUsername(), (Integer) list.get(0).getNumber(Constant.OFFSET));
                    } else {
                        Log.d(TAG, "parse list size is not correct.");
                    }
                } else {
                    e.printStackTrace();
                }
            }
        });
        updateStateOnParse(state);
    }

    private void updateMusic(String url, final String channel, final int offest){
        Log.d(TAG, "updateMusic, " + url + ", " + channel + ", " + offest);
        mediaPlayer.reset();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        try {
            mediaPlayer.setDataSource(url);
        } catch (IOException e) {
            e.printStackTrace();
        }
        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                Log.d(TAG, "onPrepared");
                JSONObject message = new JSONObject();
                try {
                    message.put("from", ParseUser.getCurrentUser().getUsername());
                    message.put("broadcast", "ok");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                pubnub.publish(channel, message, publishCallback);
                try {
                    pubnub.subscribe(channel, subscribeCallback);
                } catch (PubnubException e) {
                    e.printStackTrace();
                }
                mp.seekTo(offest);
                mp.start();
            }
        });
        try {
            mediaPlayer.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
//        Log.d(TAG, "onPrepared");
//        JSONObject message = new JSONObject();
//        try {
//            message.put("from", ParseUser.getCurrentUser().getUsername());
//            message.put("broadcast", "ok");
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//        pubnub.publish(channel, message, publishCallback);
//        try {
//            pubnub.subscribe(channel, subscribeCallback);
//        } catch (PubnubException e) {
//            e.printStackTrace();
//        }
//        mediaPlayer.seekTo(offest);
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                nextSong();
            }
        });
//        playSong();
    }

    private void updateInfoByParse(String songName) {
        songNameTextView.setText(songName);
    }

    private void vibrateNotification(int times){
        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        switch (times){
            case 2:
                vibrator.vibrate(Constant.pattern_two, -1);
                break;
            case 3:
                vibrator.vibrate(Constant.pattern_three, -1);
                break;
            case -1:
                vibrator.vibrate(Constant.pattern_four, -1);
                break;
            default:
                Log.d(TAG, "error vibrator argument");
                break;
        }

    }

    private void updateStateOnParse(final int i) {
        ParseUser user = ParseUser.getCurrentUser();
        user.put(Constant.SONG, list.get(songIndex).getName());
        final int randomNumber = randomAcceptState();
        if(i == 1) {
            user.put(Constant.ACCEPT_STATE, randomNumber);
            user.put(Constant.OFFSET, mediaPlayer.getCurrentPosition());
            startPubNubChannel();
        }
        user.put(Constant.STATE, i);
        user.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                Log.d(TAG, "saved");
                if (i == 1)
                    vibrateNotification(randomNumber);
            }
        });
        checkProgressStatusAndChange();
    }


    private int randomAcceptState() {
        int num = (int)((Math.random() * 3) + 1);
        if(num == 1)
            return -1;
        return num;
    }

    private void startPubNubChannel(){
        String name = ParseUser.getCurrentUser().getUsername();
        pubnubChannel = name;
        pubnub.publish(name, "Open the channel", publishCallback);
        try {
            pubnub.subscribe(name, subscribeCallback);
        } catch (PubnubException e) {
            e.printStackTrace();
        }
    }

    private void stopSong(){
        if(mediaPlayer.isPlaying()) {
            if(pubnubChannel.equals(ParseUser.getCurrentUser().getUsername())){
                publishMessage("pause");
            }
            mediaPlayer.pause();
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    playAndPauseImageView.setImageResource(R.drawable.play);
                }
            });

        }
    }
}
