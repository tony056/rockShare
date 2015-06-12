package com.example.tungying_chao.utilities;

import android.content.Intent;
import android.util.Log;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

/**
 * Created by tungying-chao on 6/5/15.
 */
public class PubNubDataManager {

    private static final String TAG = "PubNubDataManager";
    private static final String CLASS = "rockShare";
    private static final String NAME = "name";
    private static final String URL = "url";
    private static final String WIFI = "wifiName";
    private static final String SONG = "song";
    private static final String STATE = "state";
    private static final String ACCEPT_STATE = "accept_state";
    private static final String OFFSET = "offset";
    private static final String DEFAULT_PORT = "5566";
    private static final String EVENT_STATE = "event_state";


    private String username = "";
    private String wifiName = "";
    private String url = "";
    private String song = "";
    private int state = 0;
    private int acceptState = -1;
    private int offset = 0;

    public PubNubDataManager() {
       this.url = getUrl();
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

    public String getUsername() {
        return username;
    }

    public String getWifiName() {
        return wifiName;
    }

    public String getSong() {
        return song;
    }

    public int getState() {
        return state;
    }

    public int getOffset() {
        return offset;
    }

    public int getAcceptState() {
        return acceptState;
    }

    public void setAcceptState(int acceptState) {
        this.acceptState = acceptState;
    }

    public void setWifiName(String wifi){
        this.wifiName = wifi;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setSong(String song) {
        this.song = song;
    }

    public void setState(int state) {
        this.state = state;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public void updateState(int state){
        setState(state);
        updateDataState(this.state);
    }

    public void updateSong(String songName){
        setSong(songName);
        updateDataSong(this.song);
    }

    public void updateOffset(int offset){
        setOffset(offset);
        updateDataOffset(this.offset);
    }

    private void updateDataState(final int state){

    }

    private void updateDataSong(final String song){

    }

    private void updateDataOffset(final int offSet){

    }

    public JSONObject getCurrentToken(){
        Log.d(TAG, "" + this.username + ", " + this.wifiName + ", " + this.url + ", " + this.state + ", " + this.acceptState + ", " + this.offset + ", " + this.song);
        JSONObject dataObject = new JSONObject();
        try {
            dataObject.put(NAME, this.username);
            dataObject.put(URL, this.url);
            dataObject.put(WIFI, this.wifiName);
            dataObject.put(STATE, this.state);
            dataObject.put(ACCEPT_STATE, this.acceptState);
            dataObject.put(SONG, this.song);
            dataObject.put(OFFSET, this.offset);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return dataObject;
    }

    public JSONObject decodeToken(String message){
        try {
            JSONObject object = new JSONObject(message);
            String name = object.getString(NAME);
            String url = object.getString(URL);
            String wifi = object.getString(WIFI);
            int state = object.getInt(STATE);
            int acceptState = object.getInt(ACCEPT_STATE);
            int offset = object.getInt(OFFSET);

            JSONObject returnObject = new JSONObject();
            int event = 0;
            returnObject.put(EVENT_STATE, event);
            returnObject.put(NAME, name);
            returnObject.put(URL, url);
            returnObject.put(OFFSET, offset);

            if(!name.equals(getUsername()) && !url.equals(getUrl())){
                if(wifi.equals(getWifiName())){
                    if(state != 0) {
                        if (state == getAcceptState() && getState() == 1) {
                            returnObject.put(EVENT_STATE, 1);
                        }
                        if(state == 4)
                            returnObject.put(EVENT_STATE, 2);
                    }
                    return returnObject;
//                    sendDecodeTokenEvent(state, offset);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;

    }


    private void sendDecodeTokenEvent(int state, int offset){

    }
}
