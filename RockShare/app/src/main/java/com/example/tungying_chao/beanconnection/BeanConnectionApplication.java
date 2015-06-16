package com.example.tungying_chao.beanconnection;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.StrictMode;
import android.util.Log;
import android.widget.Toast;

import com.example.tungying_chao.utilities.MyHttpd;
import com.example.tungying_chao.utilities.PubNubDataManager;
import com.example.tungying_chao.utilities.RockShareServerHandler;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParsePush;
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

import nl.littlerobots.bean.Bean;
import nl.littlerobots.bean.BeanListener;

/**
 * Created by tungying-chao on 5/29/15.
 */
public class BeanConnectionApplication extends Application {
    private static final String TAG = "BeanConnection";
    public static final String TOUCH_EVENT = "BEAN_TOUCH_EVENT";
    public static final String SHARE_EVENT = "SHARE_EVENT";
    public static final String EVENT = "EVENT";
    private static final String CHANNEL = "rock_share";
    private static final String NAME = "name";
    private static final String URL = "url";
    private static final String WIFI = "wifiName";
    private static final String SONG = "song";
    private static final String STATE = "state";
    private static final String ACCEPT_STATE = "accept_state";
    private static final String OFFSET = "offset";
    private static final String DEFAULT_PORT = "5566";

    private Bean myBean;
    private Context context;
    private MyHttpd httpdServer;

    private PubNubDataManager pubNubDataManager;
    private RockShareServerHandler rockShareServerHandler;
    private List<JSONObject> pubNubTokenLists = new ArrayList<JSONObject>();

    public Pubnub getMyPubNub() {
        return myPubNub;
    }

    public void setMyPubNub(Pubnub myPubNub) {
        this.myPubNub = myPubNub;
    }

    private Pubnub myPubNub = new Pubnub("pub-c-9ff0b4eb-292b-46a0-bf5d-90b79ed90768", "sub-c-42c5f87e-0972-11e5-9ffb-0619f8945a4f");

    private Callback publishCallback = new Callback() {
        @Override
        public void successCallback(String channel, Object message) {
            super.successCallback(channel, message);
            Log.d(TAG, "good");
        }

        @Override
        public void errorCallback(String channel, PubnubError error) {
            super.errorCallback(channel, error);
        }

        @Override
        public void connectCallback(String channel, Object message) {
            super.connectCallback(channel, message);
        }

        @Override
        public void reconnectCallback(String channel, Object message) {
            super.reconnectCallback(channel, message);
        }

        @Override
        public void disconnectCallback(String channel, Object message) {
            super.disconnectCallback(channel, message);
        }
    };


    private BeanListener myBeanListener = new BeanListener() {
        @Override
        public void onConnected() {
            myBean.setLed(255, 255, 0);
            Log.e("Bean", "Connected");
        }

        @Override
        public void onConnectionFailed() {

        }

        @Override
        public void onDisconnected() {
            myBean.setLed(0, 0, 0);
            Log.e("Bean", "Disconnected");
        }

        @Override
        public void onSerialMessageReceived(byte[] bytes) {
//            Log.d(TAG, "length: " + bytes.length);

        }

        @Override
        public void onScratchValueChanged(int i, byte[] bytes) {
            int cmd = 0;
            if(i == 0){
                cmd = bytesToInt(bytes);
            }

            Log.d(TAG, "Get bank: " + i + ", data: " + cmd);
            sendTouchEventBroadcast(cmd);
//            Toast.makeText(getApplicationContext(), "data: " + cmd, Toast.LENGTH_SHORT).show();
        }
    };



    @Override
    public void onCreate(){
        super.onCreate();
        // Enable Local Datastore.
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        pubNubDataManager = new PubNubDataManager();
        parseInit();
        rockShareServerHandler = new RockShareServerHandler(this.context);
//        try {
//            myPubNub.subscribe(CHANNEL, subscribeCallback);
//        } catch (PubnubException e) {
//            e.printStackTrace();
//        }
        ParsePush.subscribeInBackground("", new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    Log.d("com.parse.push", "successfully subscribed to the broadcast channel.");
                } else {
                    Log.e("com.parse.push", "failed to subscribe for push", e);
                }
            }
        });
        httpServerInit();
        Log.d(TAG, "onCreate()");
//        myPubNub.publish(CHANNEL, pubNubDataManager.getCurrentToken(), publishCallback);
    }


    public Bean getMyBean() {
        if(myBean == null)
            return null;
        return myBean;
    }

    public void setMyBean(Bean myBean) {
        this.myBean = myBean;
        if(myBean != null && this.context != null){
            Log.d(TAG, "init");
            this.myBean.connect(this.context, myBeanListener);
        }
        else {
            Log.d(TAG, "fail");
        }
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = getApplicationContext();
    }

    private int bytesToInt(byte[] bytes){
        int length = bytes.length;
        int cmd = 0;
        if(length == 4){
            cmd = bytes[3] << 24 | (bytes[2] & 0xff) << 16 | (bytes[1] & 0xff) << 8
                    | (bytes[0] & 0xff);
        }
        return cmd;
    }

    private void sendTouchEventBroadcast(int value){
        Log.d(TAG, "sendTouchEventBroadcast");
        Intent intent = new Intent(TOUCH_EVENT);
        intent.putExtra(EVENT, value);
        sendBroadcast(intent);
    }

    private void sendStartToStreamInfo(JSONObject object) {
        JSONObject infoObject = object;
        try {
            if(infoObject != null) {
                int event_state = infoObject.getInt("EVENT_STATE");
//                if(event_state == 0) { // idle
                    String url = "http://" + infoObject.getString(URL);
                    String songName = infoObject.getString(SONG);
                    Intent intent = new Intent(SHARE_EVENT);
                    intent.putExtra("EVENT_STATE", event_state);
                    intent.putExtra(URL, url);
                    intent.putExtra(SONG, songName);
//                }else if(event_state == 2) //someone ask to share
                sendBroadcast(intent);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public RockShareServerHandler getRockShareServerHandler() {
        return rockShareServerHandler;
    }

    public void setRockShareServerHandler(RockShareServerHandler rockShareServerHandler) {
        this.rockShareServerHandler = rockShareServerHandler;
    }

    private Callback subscribeCallback = new Callback() {
        @Override
        public void successCallback(String channel, Object message) {
//            super.successCallback(channel, message);
            if(channel.equals(CHANNEL)){
//                JSONObject jsonObject =  pubNubDataManager.decodeToken(message.toString());

//                sendStartToStreamInfo(jsonObject);
            }
        }

        @Override
        public void errorCallback(String channel, PubnubError error) {
            super.errorCallback(channel, error);
        }

        @Override
        public void connectCallback(String channel, Object message) {
            super.connectCallback(channel, message);
        }

        @Override
        public void reconnectCallback(String channel, Object message) {
            super.reconnectCallback(channel, message);
        }

        @Override
        public void disconnectCallback(String channel, Object message) {
            super.disconnectCallback(channel, message);
        }
    };

    public PubNubDataManager getPubNubDataManager() {
        return pubNubDataManager;
    }

    public void broadcastToken(){
        myPubNub.publish(CHANNEL, pubNubDataManager.getCurrentToken(), publishCallback);
    }

    private void decodeToken(String info){
        boolean isExist = false;
        try {
            JSONObject token = new JSONObject(info);
            for(JSONObject object : pubNubTokenLists){
                if(object.getString(URL).equals(token.getString(URL))){
                    if(object.getString(STATE) == token.getString(STATE)){
                        isExist = true;
                    }else {
                        pubNubTokenLists.remove(object);
                    }
                }
            }
            if(!isExist){
                pubNubTokenLists.add(token);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
    public List<JSONObject> getPubNubTokenLists() {
        return pubNubTokenLists;
    }

    private void parseInit(){
        Parse.enableLocalDatastore(this);
        Parse.initialize(this, "vPrTWP92aurD2s7K2f83wg0PZ6h49KtJ9Z68fzBQ", "ibTJC42hUleivVSORiEVRqG6ruX5qzgAOg1MjClK");
        if(ParseUser.getCurrentUser() != null) {
            Log.d(TAG, "logOut");
            ParseUser.logOut();
        }

    }

    private void httpServerInit(){
        httpdServer = new MyHttpd(5566, getApplicationContext());
        try {
            httpdServer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ParseInstallation getParseInstallation() {
        return ParseInstallation.getCurrentInstallation();
    }

}
