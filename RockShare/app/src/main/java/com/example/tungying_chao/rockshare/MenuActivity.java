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
import android.widget.ImageButton;
import android.widget.Toast;

import com.andexert.library.RippleView;
import com.example.tungying_chao.beanconnection.BeanConnectionApplication;
import com.gc.materialdesign.views.ButtonIcon;
import com.gc.materialdesign.views.ButtonRectangle;

import nl.littlerobots.bean.Bean;


/**
 * Created by tungying-chao on 5/29/15.
 */
public class MenuActivity extends Activity {

    private static final String TAG = "MenuActivity";
    private ImageButton discoverButton;
    private ImageButton musicButton;
    private ImageButton aboutButton;

    private RippleView discoverRippleView;
    private RippleView musicRippleView;
    private RippleView aboutRippleView;

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

    private RippleView.OnRippleCompleteListener rippleViewCompleteListener = new RippleView.OnRippleCompleteListener(){

        @Override
        public void onComplete(RippleView rippleView) {
            Log.d(TAG, "onComplete");
            int viewId = rippleView.getId();
            if(viewId == R.id.discover)
                clickDiscover(rippleView);
            else if(viewId == R.id.listen)
                clickMusic(rippleView);
            else
                clickAbout(rippleView);
        }
    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu_activity);
        discoverRippleView = (RippleView)findViewById(R.id.discover);
        musicRippleView = (RippleView)findViewById(R.id.listen);
        aboutRippleView = (RippleView)findViewById(R.id.about);
        discoverRippleView.setOnRippleCompleteListener(rippleViewCompleteListener);
        musicRippleView.setOnRippleCompleteListener(rippleViewCompleteListener);
        aboutRippleView.setOnRippleCompleteListener(rippleViewCompleteListener);

        discoverButton = (ImageButton)findViewById(R.id.discoverButton);
        musicButton = (ImageButton)findViewById(R.id.musicButton);
        aboutButton = (ImageButton)findViewById(R.id.aboutButton);


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

    public void clickDiscover(View view){
        Intent intent = new Intent();
        intent.setClass(getApplicationContext(), UserListActivity.class);
        startActivity(intent);
    }

    public void clickMusic(View view){
        Intent intent = new Intent();
        intent.setClass(getApplicationContext(), MusicListActivity.class);
        startActivity(intent);
    }

    public void clickAbout(View view){
        Intent intent = new Intent();
        intent.setClass(getApplicationContext(), AboutActivity.class);
        startActivity(intent);
    }



}
