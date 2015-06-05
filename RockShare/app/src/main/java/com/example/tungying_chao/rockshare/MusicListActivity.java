package com.example.tungying_chao.rockshare;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;


import com.example.tungying_chao.beanconnection.BeanConnectionApplication;
import com.example.tungying_chao.utilities.MusicItemAdapter;
import com.parse.ParseObject;

/**
 * Created by tungying-chao on 5/30/15.
 */
public class MusicListActivity extends Activity {
    private ListView musicListView;
    private MusicItemAdapter adapter;

    private ListView.OnItemClickListener mClickListener = new ListView.OnItemClickListener(){

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            tempClick();
        }


    };

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
//                Toast.makeText(getApplicationContext(), event, Toast.LENGTH_SHORT).show();
//                cmdTextView.setText(event);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.music_list_activity);
//        cmdTextView = (TextView)findViewById(R.id.);
        musicListView = (ListView)findViewById(R.id.musicListView);
        adapter = new MusicItemAdapter(getApplicationContext());
        musicListView.setAdapter(adapter);
        musicListView.setOnItemClickListener(mClickListener);
//        ParseObject rockShareObject = new ParseObject("TestObject");
//        rockShareObject.put("foo", "bar");
//        rockShareObject.saveInBackground();
    }

    @Override
    public void onPause(){
        super.onPause();
        unregisterReceiver(mBroadcastReceiver);
    }

    @Override
    public void onResume(){
        super.onResume();
        registerReceiverForBeanTouchEvent();
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

    private void registerReceiverForBeanTouchEvent(){
        IntentFilter mFilter = new IntentFilter(BeanConnectionApplication.TOUCH_EVENT);
        registerReceiver(mBroadcastReceiver, mFilter);
    }

    private void tempClick(){
        Intent intent = new Intent();
        intent.setClass(getApplicationContext(), MusicPlayerActivity.class);
        startActivity(intent);
    }

}
