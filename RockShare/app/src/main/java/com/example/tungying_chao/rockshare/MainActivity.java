package com.example.tungying_chao.rockshare;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;


import com.example.tungying_chao.beanconnection.BeanConnectionApplication;

import java.util.ArrayList;
import java.util.List;

import nl.littlerobots.bean.Bean;
import nl.littlerobots.bean.BeanDiscoveryListener;
import nl.littlerobots.bean.BeanManager;


public class MainActivity extends Activity {
    private BeanDiscoveryListener mBeanDiscoveryListener = new BeanDiscoveryListener() {
        @Override
        public void onBeanDiscovered(Bean bean) {
//            helloText.setText("Name: " + bean.getDevice().getName().toString());
            Log.e("Bean", "Getbean");
            mBeanList.add(bean);
            beanDevices.add(bean.getDevice().getName().toString());
            adapter.notifyDataSetChanged();
        }

        @Override
        public void onDiscoveryComplete() {
            Log.e("Bean", "Completed");
        }
    };

    private ListView mBeanListView;
    private List<String> beanDevices = new ArrayList<String>();
    private List<Bean> mBeanList = new ArrayList<Bean>();
    private ArrayAdapter<String> adapter;
    private boolean isConnected = false;
    public Bean bean;
    private ListView.OnItemClickListener mListViewClickListener = new ListView.OnItemClickListener(){

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            String beanName = parent.getItemAtPosition(position).toString();
            if(mBeanList.get(position).getDevice().getName().toString().equals(beanName)){
                bean = mBeanList.get(position);
                Bean backgroundBean = ((BeanConnectionApplication)getApplicationContext()).getMyBean();
                if(backgroundBean == null){
                    Log.e("Bean", "connecting");
                    ((BeanConnectionApplication)getApplicationContext()).setContext(getApplicationContext());
                    ((BeanConnectionApplication)getApplicationContext()).setMyBean(bean);
                }
                Intent intent = new Intent();
                intent.setClass(getApplicationContext(), MenuActivity.class);
                startActivity(intent);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bean_connection);
        mBeanListView = (ListView)findViewById(R.id.listView);
        BeanManager.getInstance().startDiscovery(mBeanDiscoveryListener);
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, beanDevices);
        mBeanListView.setAdapter(adapter);
        mBeanListView.setOnItemClickListener(mListViewClickListener);
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


}
