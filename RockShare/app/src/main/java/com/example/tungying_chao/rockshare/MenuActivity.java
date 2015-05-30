package com.example.tungying_chao.rockshare;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.tungying_chao.beanconnection.BeanConnectionApplication;

import nl.littlerobots.bean.Bean;


/**
 * Created by tungying-chao on 5/29/15.
 */
public class MenuActivity extends Activity {

    private static final String TAG = "MenuActivity";
    private Button mBtnOn;
    private Button mBtnOff;
    private boolean isOn = false;
    private Bean myBean;
    private Button.OnClickListener tempBtnClickListener;

    {
        tempBtnClickListener = new Button.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (v.getId() == R.id.ledOff) {
                    Intent intent = new Intent();
                    intent.setClass(getApplicationContext(), MusicListActivity.class);
                    startActivity(intent);
                } else {
                    if (isOn) {
                        myBean.setLed(0, 0, 255);
                    } else {
                        myBean.setLed(0, 255, 0);
                    }
                    isOn = !isOn;
                }
            }
        };
    }
    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String event = "";
            if(intent.hasExtra(BeanConnectionApplication.EVENT)){
                int cmd = intent.getIntExtra(BeanConnectionApplication.EVENT, 0);
                switch (cmd){
                    case 1:
                        event = "Single Click";
                        break;
                    case 2:
                        event = "Double Click";
                        break;
                    case 3:
                        event = "Triple Click";
                        break;
                    case -1:
                        event = "Long Click";
                        break;
                    default:
                        break;
                }
                if(event.length() == 0)
                    return;
                Toast.makeText(getApplicationContext(), event, Toast.LENGTH_SHORT).show();
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu_activity);
        mBtnOn = (Button)findViewById(R.id.ledOn);
        mBtnOff = (Button)findViewById(R.id.ledOff);

        if(((BeanConnectionApplication)getApplicationContext()).getMyBean() != null){
            myBean = ((BeanConnectionApplication)getApplicationContext()).getMyBean();
        }
        mBtnOn.setOnClickListener(tempBtnClickListener);
        mBtnOff.setOnClickListener(tempBtnClickListener);
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

    @Override
    public void onDestroy(){

        super.onDestroy();

        Log.d(TAG, "onDestroy()");
    }

    @Override
    public void onPause() {
        Log.d(TAG, "onPause()");
        super.onPause();
        unregisterReceiver(mBroadcastReceiver);
    }

    @Override
    public void onResume(){
        Log.d(TAG, "onResume");
        super.onResume();
        registerReceiverForBeanTouchEvent();
    }


    private void registerReceiverForBeanTouchEvent(){
        IntentFilter mFilter = new IntentFilter(BeanConnectionApplication.TOUCH_EVENT);
        registerReceiver(mBroadcastReceiver, mFilter);
    }


}
