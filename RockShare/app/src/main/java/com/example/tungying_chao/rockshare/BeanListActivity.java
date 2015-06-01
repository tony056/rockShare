package com.example.tungying_chao.rockshare;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.tungying_chao.beanconnection.BeanConnectionApplication;
import com.example.tungying_chao.utilities.BeanItemAdapter;

import java.util.ArrayList;
import java.util.List;

import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;
import in.srain.cube.views.ptr.header.MaterialHeader;
import in.srain.cube.views.ptr.util.PtrLocalDisplay;
import nl.littlerobots.bean.Bean;
import nl.littlerobots.bean.BeanDiscoveryListener;
import nl.littlerobots.bean.BeanManager;

/**
 * Created by tungying-chao on 6/1/15.
 */
public class BeanListActivity extends Activity {
    private static final String TAG = "BeanListActivity";
    private BeanDiscoveryListener mBeanDiscoveryListener = new BeanDiscoveryListener() {
        @Override
        public void onBeanDiscovered(Bean bean) {
//            helloText.setText("Name: " + bean.getDevice().getName().toString());
            Log.e("Bean", "Getbean");
            mBeanList.add(bean);
//            beanDevices.add(bean.getDevice().getName().toString());
//            adapter.notifyDataSetChanged();
        }

        @Override
        public void onDiscoveryComplete() {
            Log.e("Bean", "Completed");
        }
    };

    private ListView mBeanListView;
//    private List<String> beanDevices = new ArrayList<String>();
    private List<Bean> mBeanList = new ArrayList<Bean>();
//    private ArrayAdapter<String> adapter;
    private BeanItemAdapter adapter;
    private int prevListLength = 0;
    public Bean bean;
    private ListView.OnItemClickListener mListViewClickListener = new ListView.OnItemClickListener(){

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            String beanName = parent.getItemAtPosition(position).toString();
            Log.d(TAG, "clicked" + beanName);
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

    };
    private PtrFrameLayout mPtrFrameLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bean_connection);
        mBeanListView = (ListView)findViewById(R.id.listView);
        BeanManager.getInstance().startDiscovery(mBeanDiscoveryListener);
//        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, beanDevices);
        adapter = new BeanItemAdapter(this, mBeanList);
        mBeanListView.setAdapter(adapter);
        mBeanListView.setOnItemClickListener(mListViewClickListener);
        mPtrFrameLayout = (PtrFrameLayout)findViewById(R.id.bean_connection_list);

        final MaterialHeader header = new MaterialHeader(getApplicationContext());
        int[] colors = getResources().getIntArray(R.array.google_colors);
        header.setColorSchemeColors(colors);
        header.setLayoutParams(new PtrFrameLayout.LayoutParams(-1, -2));
        header.setPadding(0, PtrLocalDisplay.dp2px(15), 0, PtrLocalDisplay.dp2px(10));
        header.setPtrFrameLayout(mPtrFrameLayout);

        mPtrFrameLayout.setLoadingMinTime(1000);
        mPtrFrameLayout.setDurationToCloseHeader(1500);
        mPtrFrameLayout.setHeaderView(header);
        mPtrFrameLayout.addPtrUIHandler(header);
        mPtrFrameLayout.postDelayed(new Runnable() {
            @Override
            public void run() {
                mPtrFrameLayout.autoRefresh(false);
            }
        }, 100);

        mPtrFrameLayout.setPtrHandler(new PtrHandler() {
            @Override
            public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {
                return true;
            }

            @Override
            public void onRefreshBegin(final PtrFrameLayout frame) {
                Log.d("Main", "onRefresh");
//                if(prevListLength != beanDevices.size()){
//                    Log.d(TAG, "diff");
//
//                }else{
//                    Log.d(TAG, "diff: " + prevListLength + ", " + beanDevices.size());
//
//                }

                frame.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        frame.refreshComplete();
                        if(prevListLength != mBeanList.size()) {
                            Log.d(TAG, "" + prevListLength + ", " + mBeanList.size());
                            adapter.notifyDataSetChanged();
                            prevListLength = mBeanList.size();
                        }

                    }
                }, 0);
//                if (mImageHasLoaded) {
//                    long delay = (long) (1000 + Math.random() * 2000);
//                    delay = Math.max(0, delay);
//                    delay = 0;
//                    frame.postDelayed(new Runnable() {
//                        @Override
//                        public void run() {
//                            frame.refreshComplete();
//                        }
//                    }, delay);
//                } else {
//                    mStartLoadingTime = System.currentTimeMillis();
//                    imageView.loadImage(imageLoader, mUrl);
//                }
            }
        });
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
