package com.example.tungying_chao.rockshare;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.example.tungying_chao.beanconnection.BeanConnectionApplication;
import com.example.tungying_chao.utilities.PubNubDataManager;
import com.example.tungying_chao.utilities.RockShareServerHandler;
import com.gc.materialdesign.views.ButtonFlat;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.ArrayList;
import java.util.List;

import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;
import in.srain.cube.views.ptr.header.MaterialHeader;
import in.srain.cube.views.ptr.util.PtrLocalDisplay;
import nl.littlerobots.bean.Bean;
import nl.littlerobots.bean.BeanDiscoveryListener;
import nl.littlerobots.bean.BeanManager;


public class MainActivity extends Activity {
    private static final String TAG = "MainActivity";
    private static final String USER = "USER";
    private static final String NICK_NAME = "NICK_NAME";



    private EditText nickNameEditText;
    private ButtonFlat nickNameEnterButton;

    private SharedPreferences nickNamePreference;
    private Button.OnClickListener mOnClickListener = new Button.OnClickListener(){

        @Override
        public void onClick(View v) {
            saveNickName();
        }
    };
    private RockShareServerHandler rockShareServerHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        nickNameEditText = (EditText)findViewById(R.id.nickNameEditText);
        nickNameEnterButton = (ButtonFlat)findViewById(R.id.enterNickNameButton);
        nickNameEnterButton.setOnClickListener(mOnClickListener);
        rockShareServerHandler = ((BeanConnectionApplication)getApplicationContext()).getRockShareServerHandler();
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

    private void saveNickName(){
        nickNamePreference = getSharedPreferences(USER, -1);
        String name = nickNameEditText.getText().toString();
        if(name.length() > 0) {
            nickNamePreference.edit().putString(NICK_NAME, name).commit();
            Log.d(TAG, "name: " + nickNamePreference.getString(NICK_NAME, "null"));
        } else{
            Toast.makeText(getApplicationContext(),"Please enter your name!", Toast.LENGTH_SHORT).show();
            return;
        }
        initParseInstallation(name);
        goToNextActivity();
    }

    private void initParseInstallation(String name) {
        if (rockShareServerHandler == null){
            Log.d(TAG, "null handler");
            return;
        }
        setWifiName();
        rockShareServerHandler.initNewData(name);

    }

    private void setTokenName(String name) {
        PubNubDataManager pubNubDataManager = ((BeanConnectionApplication)getApplicationContext()).getPubNubDataManager();
        if(pubNubDataManager != null){
            pubNubDataManager.setUsername(name);
        }else {
            Log.d(TAG, "null pointer");
        }
    }

    private void goToNextActivity(){
        Intent intent = new Intent();
        intent.setClass(getApplicationContext(), BeanListActivity.class);
        startActivity(intent);
    }

    private void setWifiName(){
        WifiManager manager = (WifiManager)this.getSystemService(Context.WIFI_SERVICE);
        String wifiName = manager.getConnectionInfo().getSSID();
        rockShareServerHandler.setWifiName(wifiName);
    }

//    private void sendInfoToParse(String name){
//        ((BeanConnectionApplication)getApplicationContext()).getRockShareServerHandler().initNewData(name);
//    }

}
