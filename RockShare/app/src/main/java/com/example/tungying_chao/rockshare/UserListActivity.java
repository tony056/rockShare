package com.example.tungying_chao.rockshare;

import android.app.Activity;
import android.app.ProgressDialog;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.tungying_chao.beanconnection.BeanConnectionApplication;
import com.example.tungying_chao.utilities.Constant;
import com.example.tungying_chao.utilities.RockShareServerHandler;
import com.example.tungying_chao.utilities.UserItemAdapter;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
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
 * Created by tungying-chao on 6/1/15.
 */
public class UserListActivity extends Activity {

    private static final String TAG = "UserListActivity";
    private static final String WIFI = "wifiName";
    private ListView userListView;
    private ProgressDialog mProgressDialog;
    private List<ParseUser> users;
    List<ParseUser> list = new ArrayList<ParseUser>();
    private RockShareServerHandler rockShareServerHandler;
    private ProgressDialog waitForResponseDialog;
    Pubnub pubnub;
    private MediaPlayer mediaPlayer;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_list_activity);
        rockShareServerHandler = ((BeanConnectionApplication)getApplicationContext()).getRockShareServerHandler();
        initWaitForResponseDialog();
        new RemoteDataTask().execute();
        pubnub = ((BeanConnectionApplication)getApplicationContext()).getMyPubNub();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
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

    private void initWaitForResponseDialog(){
        waitForResponseDialog = new ProgressDialog(UserListActivity.this);
        waitForResponseDialog.setTitle("Waiting for the response");
        waitForResponseDialog.setMessage("Please wait...");
        waitForResponseDialog.setIndeterminate(true);
//        waitForResponseDialog.show();

    }

//            michael
    Callback subscribeCallback = new Callback() {
        @Override
        public void successCallback(String channel, Object message) {
//            super.successCallback(channel, message);
            Log.d(TAG, message.toString());
            try {
                JSONObject object = new JSONObject(message.toString());
//                not sure with the key
                String status = object.getString("broadcast");
                switch (status){
                    case "Accept":
                        userAcceptSharing(channel);
                        break;

                    case "Reject":
                        userRejectSharing();
                        break;

                    case "play":
                        mediaPlayer.start();
                        break;

                    case "pause":
                        mediaPlayer.pause();
                        break;

                }
            } catch (JSONException e) {
                Log.d(TAG, "Subscribe callback error");
                e.printStackTrace();
            }
        }

        @Override
        public void errorCallback(String channel, PubnubError error) {
            super.errorCallback(channel, error);
            Log.d(TAG, "error");
        }
    };

// michael
    private void initMediaPlayer() {
        Log.d(TAG, "initMediaPlayer");
        mediaPlayer.reset();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        try {
//            still require song name to get source
//            mediaPlayer.setDataSource(list.get(index).getPath());

//            [ additional ]
//            update song's info if want to show
//            suggestions: use dialog
            mediaPlayer.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mediaPlayer.stop();
            }
        });
    }

//    private void playSong(){
//        if(pubnubChannel.equals(ParseUser.getCurrentUser().getUsername()) && !mediaPlayer.isPlaying()){
//            publishMessage("play");
//        }
//        mediaPlayer.start();
////        rockShareServerHandler.updateSong(list.get(songIndex).getName());
////        rockShareServerHandler.updateOffset(mediaPlayer.getCurrentPosition());
//
//        runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                playAndPauseImageView.setImageResource(R.drawable.pause);
//            }
//        });
//    }

//    michael
//    get song name and offset
    private void getSongInfo(String name) {
        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.whereEqualTo(Constant.NAME, name);
//        query.orderByAscending("")
        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> list, ParseException e) {
                if (e == null) {
                    if (list.size() == 1) {
                        Log.d(TAG, "Find user");
//                        pubnubChannel = list.get(0).getUsername();
                        updateMusic(list.get(0).getString(Constant.URL), list.get(0).getUsername(), (Integer) list.get(0).getNumber(Constant.OFFSET));
                    } else {
                        Log.d(TAG, "Update user fail");
                    }
                } else {
                    e.printStackTrace();
                }
            }
        });
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
                try {
                    pubnub.subscribe(channel, subscribeCallback);
                } catch (PubnubException e) {
                    e.printStackTrace();
                }
                mp.seekTo(offest);
            }
        });
        try {
            mediaPlayer.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mediaPlayer.stop();
            }
        });
//        playSong();
    }

    private void userRejectSharing() {
        waitForResponseDialog.dismiss();
        Log.d(TAG, "Reject");
        // toast
    }

    private void userAcceptSharing(String name) {
        waitForResponseDialog.dismiss();
        Log.d(TAG, "Accept");
//        michael
//        get song information on parse
        getSongInfo(name);

//        intent to get song
        initMediaPlayer();

    }

    private class RemoteDataTask extends AsyncTask<Void, Void, Void>{

        @Override
        protected Void doInBackground(Void... params) {
            ParseQuery<ParseUser> query = ParseUser.getQuery();
//            ParseQuery<ParseInstallation> query = ParseInstallation.getQuery();
//            query.whereEqualTo("installationId", ParseInstallation.getCurrentInstallation().getInstallationId());
            try {
                users = query.find();
            } catch (ParseException e) {
                e.printStackTrace();
            }
            return null;
        }


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressDialog = new ProgressDialog(UserListActivity.this);
            mProgressDialog.setTitle("Searching people around you");
            mProgressDialog.setMessage("Please wait...");
            mProgressDialog.setIndeterminate(true);
            mProgressDialog.show();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            userListView = (ListView)findViewById(R.id.userListView);

            for(ParseUser obj: users){
//                if(!obj.getString("installationId").equals(ParseInstallation.getCurrentInstallation().getInstallationId())){
//                if(!obj.getUsername().equals(((BeanConnectionApplication)getApplicationContext()).getRockShareServerHandler().getUsername())){
                    list.add(obj);
//                }
            }

            UserItemAdapter adapter = new UserItemAdapter(getApplicationContext(), list);
            userListView.setAdapter(adapter);
            mProgressDialog.dismiss();
            userListView.setOnItemClickListener(new ListView.OnItemClickListener(){

                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Log.d(TAG, "clicked");
                    ParseUser user = list.get(position);
                    Log.d("TAG", user.getUsername());
                    if(user.getInt("state") == 0) {
                        rockShareServerHandler.sendShareRequest(user);
                        try {
                            pubnub.subscribe(user.getUsername(), subscribeCallback);
                        } catch (PubnubException e) {
                            e.printStackTrace();
                        }
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                waitForResponseDialog.show();
                            }
                        });
                    }
                    else
                        Toast.makeText(getApplicationContext(), user.getString("username") + " cannot share with you right now!", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
