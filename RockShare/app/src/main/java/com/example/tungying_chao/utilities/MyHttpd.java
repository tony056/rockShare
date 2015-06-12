package com.example.tungying_chao.utilities;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;
import android.util.Log;

import com.example.tungying_chao.beanconnection.BeanConnectionApplication;
import com.parse.ParseInstallation;
import com.parse.ParseUser;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

import fi.iki.elonen.NanoHTTPD;

/**
 * Created by tungying-chao on 6/7/15.
 */
public class MyHttpd extends NanoHTTPD {


    private Context context;

    public MyHttpd(int port, Context context) {
        super(port);
        this.context = context;
        getLocalIPAddress();
    }

    public MyHttpd(String hostname, int port) {
        super(hostname, port);
    }

    @Override
    public Response serve(IHTTPSession session) {
        FileInputStream file = null;
//        getLocalIPAddress();
        Log.d("Response", session.toString());
        try {
            String str = "";
            File files[] = Environment.getExternalStorageDirectory().listFiles();
            for(File fil : files){
                str += fil.toString() + "\n";
            }
            Log.d("fileList", str);
            Log.d("http", Environment.getExternalStorageDirectory().toString());
            file = new FileInputStream(Environment.getExternalStorageDirectory() + "/" + getCurrentMusic());
//            file = new FileInputStream(Environment.getExternalStorageDirectory() + "/Cool.mp3");
//            if(file == null){
//                Log.d("QQ", "no file");
//            }else{
//                File checkFile = new File(Environment.getExternalStorageDirectory() + "/" + getCurrentMusic());
//                Log.d("QQ", "exist or not: " + checkFile.exists());
//            }
        }catch (FileNotFoundException e){
            e.printStackTrace();
        }catch (Exception e){
            Log.d("QQ", e.getMessage());
        }
        return new NanoHTTPD.Response(Response.Status.OK, "audio/mpeg", file);
    }

    public static byte[] getLocalIPAddress () {
        byte ip[]=null;
        String ipName = "";
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress()) {
                        ip= inetAddress.getAddress();
                        ipName = inetAddress.getHostAddress();

                    }
                }
            }
        } catch (SocketException ex) {
            Log.i("SocketException ", ex.toString());
        }
        Log.d("IP", ipName);
        return ip;

    }

    private String getCurrentMusic(){
        if(this.context == null){
            Log.d("HTTP", "No context");
            return "";
        }
        SharedPreferences sharedPreferences = this.context.getSharedPreferences(Constant.MEDIA_STATE, 0);
        if(sharedPreferences != null){
            String songName = sharedPreferences.getString(Constant.SONG, "no song");
            int offset = sharedPreferences.getInt(Constant.OFFSET, 0);
            ((BeanConnectionApplication) this.context).getRockShareServerHandler().updateOffset(offset);
            Log.d("current music", songName);
            return songName + ".mp3";
        }
        return "";
    }

}
