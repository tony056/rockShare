package com.example.tungying_chao.utilities;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.example.tungying_chao.rockshare.MenuActivity;
import com.example.tungying_chao.rockshare.R;
import com.parse.ParsePushBroadcastReceiver;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by tungying-chao on 6/7/15.
 */
public class RockSharePushBroadcastReceiver extends ParsePushBroadcastReceiver {
    private static final int NOTIFICATION_ID = 1;

    @Override
    public void onReceive(Context context, Intent intent) {
//        super.onPushReceive(context, intent);
        Intent intent1 = new Intent(context, MenuActivity.class);
        PendingIntent pIntent = PendingIntent.getActivity(context, 0, intent1, 0);
        JSONObject pushData = null;

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        try {
            pushData = new JSONObject(intent.getStringExtra("com.parse.Data"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String action = null;
        if(pushData != null) {
            action = intent.getAction();
//            action = pushData.optString("action", (String)null);
            if(action.equalsIgnoreCase("com.example.tungying_chao.rockshare.NEW_NOTIF")){
                Log.d("Broadcast", "received");
            }
        }

        if(action != null) {
            Bundle notification = intent.getExtras();
            Intent broadcastIntent = new Intent();
            broadcastIntent.putExtras(notification);
            broadcastIntent.setAction(action);
            broadcastIntent.setPackage(context.getPackageName());
            context.sendBroadcast(broadcastIntent);
        }

//        Notification notification1 = this.getNotification(context, intent);
        Notification notification1 = new Notification.Builder(context).setContentTitle("Testing!")
                .setContentText("hihi")
                .setContentIntent(pIntent)
                .addAction(R.drawable.ic_launcher, "Accept", pIntent).build();
        notification1.flags |= Notification.FLAG_AUTO_CANCEL;
        if(notification1 != null) {
            notificationManager.notify(NOTIFICATION_ID, notification1);
        }
    }
}
