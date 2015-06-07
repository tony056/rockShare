package com.example.tungying_chao.utilities;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;
import com.pubnub.api.Pubnub;

import org.json.JSONException;
import org.json.JSONObject;

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
    private static final String MSG_CLASS = "RequestMsg";
    private static final String NAME = "username";
    private static final String URL = "url";
    private static final String WIFI = "wifiName";
    private static final String SONG = "song";
    private static final String STATE = "state";
    private static final String ACCEPT_STATE = "accept_state";
    private static final String OFFSET = "offset";
    private static final String DEFAULT_PORT = "5566";

    private String username = "";
    private String wifiName = "";
    private String url = "";
    private String song = "";
    private int state = 0;
    private int accept_state = 0;
    private int offset = 0;
    private Context context = null;
    private WifiManager manager = null;

    private ParseInstallation parseInstallation;

    public RockShareServerHandler(Context context){
        this.context = context;
//        this.manager = (WifiManager)this.context.getSystemService(Context.WIFI_SERVICE);
        this.url = getUrl();
        this.parseInstallation = ParseInstallation.getCurrentInstallation();
        if(this.context != null) {

        }
    }

    public void initNewData(String name){
        this.username = name;
        Log.d(TAG, "" + this.username + ", " + this.wifiName + ", " + this.url + ", " + this.state + ", " + this.offset + ", " + this.song);
        ParseUser parseUser = new ParseUser();
//        ParseRelation<ParseUser> check = parseInstallation.getRelation("user");
//        if(check != null){
//            parseUser = check.getQuery();
//        }
        parseUser.setUsername(this.username);
        parseUser.setPassword("0000");
        parseUser.put(NAME, this.username);
        parseUser.put(WIFI, this.wifiName);
        parseUser.put("installationId", parseInstallation.getInstallationId());
        parseUser.put(URL, this.url);
        parseUser.put(STATE, this.state);
        parseUser.put(ACCEPT_STATE, this.accept_state);
        parseUser.put(SONG, this.song);
        parseUser.put(OFFSET, this.offset);
        ParseRelation<ParseInstallation> relation = parseUser.getRelation("installation");
        relation.add(parseInstallation);
        parseUser.signUpInBackground(new SignUpCallback() {
            @Override
            public void done(ParseException e) {
                Log.d(TAG, "signed up");
                initInstallation();

            }
        });



    }

    public void setWifiName(String wifi){
        this.wifiName = wifi;
    }

    private void initInstallation(){
        ParseInstallation parseInstallation = ParseInstallation.getCurrentInstallation();
        parseInstallation.put("username", this.username);
//        parseInstallation.put("user", ParseUser.getCurrentUser());
        parseInstallation.saveInBackground();
    }

    public String getUrl(){
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
        updateDataState();
    }

    public void updateSong(String songName){
        this.song = songName;
        updateDataSong();
    }

    public void updateOffset(int offset){
        this.offset = offset;
        updateDataOffset();
    }

    private void updateDataState(){
        ParseUser user = ParseUser.getCurrentUser();
        user.put(STATE, this.state);
        user.saveInBackground();
    }

    private void updateDataSong(){
        ParseUser user = ParseUser.getCurrentUser();
        user.put(SONG, this.song);
        user.saveInBackground();
    }

    private void updateDataOffset(){
        ParseUser user  = ParseUser.getCurrentUser();
        user.put(OFFSET, this.offset);
        user.saveInBackground();
    }

    public void setManager(WifiManager manager) {
        this.manager = manager;
    }

    public String getWifiName() {
        return wifiName;
    }

    public void sendShareRequest(ParseUser target){
        if(target != null){
            ParseObject message = new ParseObject(MSG_CLASS);
            message.put("from", parseInstallation.getString(NAME));
            message.put("to", target.getString("username") );
            message.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    Log.d(TAG, "send request succeeded");
                }
            });
        }
    }


}
