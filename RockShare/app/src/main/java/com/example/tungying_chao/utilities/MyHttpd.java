package com.example.tungying_chao.utilities;

import android.os.Environment;
import android.util.Log;

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
    public MyHttpd(int port) {
        super(port);
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
            file = new FileInputStream(Environment.getExternalStorageDirectory()+"/Cool.mp3");
        }catch (FileNotFoundException e){
            e.printStackTrace();
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
                        ipName = inetAddress.getHostName();
                    }
                }
            }
        } catch (SocketException ex) {
            Log.i("SocketException ", ex.toString());
        }
        Log.d("IP", ipName);
        return ip;

    }

}
