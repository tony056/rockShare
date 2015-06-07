package com.example.tungying_chao.rockshare;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tungying_chao.utilities.MusicItemAdapter;
import com.example.tungying_chao.utilities.SongInfo;
import com.example.tungying_chao.utilities.SongManager;
import com.gc.materialdesign.views.ButtonFlat;
import com.gc.materialdesign.views.Slider;


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
    private Dialog volumeDialog;
    private Slider volumeSlider;
    private ButtonFlat okButton;

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
                        playAndPauseImageView.setImageResource(R.drawable.pause);
                        mediaPlayer.start();
                    }else {
                        playAndPauseImageView.setImageResource(R.drawable.play);
                        mediaPlayer.pause();
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

    private void openDialog() {
//        mediaPlayer.pause();
        volumeDialog.show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.music_player_activity);
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
        initLists();
        songIndex = getSongIndex();
        initMediaPlayer(songIndex);
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        initDialog();
    }

    private void initLists() {
        SongManager songManager = new SongManager(this);
        list = songManager.getSongsList();
    }

    @Override
    public void onPause(){
        super.onPause();
        mediaPlayer = null;
    }

    @Override
    public void onResume(){
        super.onResume();
        if(mediaPlayer == null){
            mediaPlayer = new MediaPlayer();
        }else {
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
        mediaPlayer.reset();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        try {
            mediaPlayer.setDataSource(list.get(index).getPath());
            updateInfo();
            mediaPlayer.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
        mediaPlayer.start();
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

}
