package com.example.tungying_chao.beanconnection;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import nl.littlerobots.bean.Bean;
import nl.littlerobots.bean.BeanListener;

/**
 * Created by tungying-chao on 5/29/15.
 */
public class BeanConnectionApplication extends Application {
    private static final String TAG = "BeanConnection";

    //    private boolean runOrNot = true;
    private Bean myBean;
    private Context context;
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

        }

        @Override
        public void onScratchValueChanged(int i, byte[] bytes) {

        }
    };


    @Override
    public void onCreate(){
        super.onCreate();
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
}
