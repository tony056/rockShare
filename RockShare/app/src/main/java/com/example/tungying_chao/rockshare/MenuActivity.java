package com.example.tungying_chao.rockshare;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

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
                if(isOn){
                    myBean.setLed(0, 0, 255);
                }else{
                    myBean.setLed(0, 255, 0);
                }
                isOn = !isOn;
            }
        };
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu_activity);
        mBtnOn = (Button)findViewById(R.id.ledOn);


        if(((BeanConnectionApplication)getApplicationContext()).getMyBean() != null){
            myBean = ((BeanConnectionApplication)getApplicationContext()).getMyBean();
        }
        mBtnOn.setOnClickListener(tempBtnClickListener);
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
        myBean.disconnect();
        super.onDestroy();
        Log.d(TAG, "onDestroy()");
    }

}
