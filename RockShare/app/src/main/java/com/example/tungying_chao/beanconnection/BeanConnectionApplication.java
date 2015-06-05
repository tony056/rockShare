package com.example.tungying_chao.beanconnection;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.StrictMode;
import android.util.Log;
import android.widget.Toast;

import com.example.tungying_chao.utilities.RockShareServerHandler;
import com.parse.Parse;

import nl.littlerobots.bean.Bean;
import nl.littlerobots.bean.BeanListener;

/**
 * Created by tungying-chao on 5/29/15.
 */
public class BeanConnectionApplication extends Application {
    private static final String TAG = "BeanConnection";
    public static final String TOUCH_EVENT = "BEAN_TOUCH_EVENT";
    public static final String EVENT = "EVENT";

    private Bean myBean;
    private Context context;
    private RockShareServerHandler rockShareServerHandler;
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
        Parse.enableLocalDatastore(getApplicationContext());
        Parse.initialize(getApplicationContext(), "vPrTWP92aurD2s7K2f83wg0PZ6h49KtJ9Z68fzBQ", "ibTJC42hUleivVSORiEVRqG6ruX5qzgAOg1MjClK");
        rockShareServerHandler = new RockShareServerHandler(this.context);
        Log.d(TAG, "onCreate()");
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

    public RockShareServerHandler getRockShareServerHandler() {
        return rockShareServerHandler;
    }

    public void setRockShareServerHandler(RockShareServerHandler rockShareServerHandler) {
        this.rockShareServerHandler = rockShareServerHandler;
    }

}
