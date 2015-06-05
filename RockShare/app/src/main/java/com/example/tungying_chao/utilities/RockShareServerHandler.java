package com.example.tungying_chao.utilities;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.List;

/**
 * Created by tungying-chao on 6/5/15.
 */
public class RockShareServerHandler {
    private static final String TAG = "RockShareServerHandler";
    private static final String CLASS = "rockShare";
    private static final String NAME = "name";
    private static final String URL = "url";
    private static final String WIFI = "wifiName";
    private static final String SONG = "song";
    private static final String STATE = "state";
    private static final String OFFSET = "offset";
    private static final String DEFAULT_PORT = "5566";

    private String username = "";
    private String wifiName = "";
    private String url = "";
    private String song = "";
    int state = 0;
    int offset = 0;
    private Context context = null;
    private WifiManager manager = null;

    public RockShareServerHandler(Context context){
        this.context = context;
//        this.manager = (WifiManager)this.context.getSystemService(Context.WIFI_SERVICE);
        this.url = getUrl();
        if(this.context != null) {

        }
    }

    public void initNewData(String name){
        this.username = name;
        Log.d(TAG, "" + this.username + ", " + this.wifiName + ", " + this.url + ", " + this.state + ", " + this.offset + ", " + this.song);
        ParseObject parseData = new ParseObject(CLASS);
        parseData.put(NAME, username);
        parseData.put(WIFI, wifiName);
        parseData.put(URL, url);
        parseData.put(STATE, state);
        parseData.put(OFFSET, offset);
        parseData.put(SONG, song);
        parseData.saveInBackground();

    }

    public void setWifiName(String wifi){
        this.wifiName = wifi;
    }

    private String getUrl(){
        String returnUrl = "";
        try {
            for(Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();){
                NetworkInterface intf = en.nextElement();
                for(Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();){
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if(!inetAddress.isLoopbackAddress()){
                        returnUrl = inetAddress.getHostName();
                    }
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
        Log.d(TAG, "getURl");
        return returnUrl + ":" + DEFAULT_PORT;
    }

    public void updateState(int state){
        this.state = state;
        updateDataState(this.state);
    }

    public void updateSong(String songName){
        this.song = songName;
        updateDataSong(this.song);
    }

    public void updateOffset(int offset){
        this.offset = offset;
        updateDataOffset(this.offset);
    }

    private void updateDataState(final int state){
        ParseQuery<ParseObject> query = ParseQuery.getQuery(CLASS);
        query.whereEqualTo(URL, this.url);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {
                if (e == null) {
                    if (list.size() == 1) {
                        ParseObject object = list.get(0);
                        object.put(STATE, state);
                        object.saveInBackground();
                    }
                } else {
                    Log.d(TAG, "parse update state error");
                }
            }
        });
    }

    private void updateDataSong(final String song){
        ParseQuery<ParseObject> query = ParseQuery.getQuery(CLASS);
        query.whereEqualTo(URL, this.url);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {
                if(e == null){
                    if(list.size() == 1){
                        ParseObject object = list.get(0);
                        object.put(SONG, song);
                        object.saveInBackground();
                    }
                }else {
                    Log.d(TAG, "parse update song error");
                }
            }
        });
    }

    private void updateDataOffset(final int offSet){
        ParseQuery<ParseObject> query = ParseQuery.getQuery(CLASS);
        query.whereEqualTo(URL, this.url);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {
                if(e == null){
                    if(list.size() == 1){
                        ParseObject object = list.get(0);
                        object.put(OFFSET, offSet);
                        object.saveInBackground();
                    }
                }else {
                    Log.d(TAG, "parse update offset error");
                }
            }
        });
    }

    public void setManager(WifiManager manager) {
        this.manager = manager;
    }


}
