package com.example.tungying_chao.rockshare;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.tungying_chao.beanconnection.BeanConnectionApplication;
import com.example.tungying_chao.utilities.Constant;
import com.example.tungying_chao.utilities.RockShareServerHandler;
import com.gc.materialdesign.widgets.Dialog;
import com.parse.ParseObject;
import com.parse.ParseUser;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by tungying-chao on 6/7/15.
 */
public class CheckActivity extends Activity {
    private static final String TAG = "CheckActivity";
    private static final String ACC_CLASS = "AcceptMsg";
    JSONObject object;

    Dialog dialog;
    View.OnClickListener acceptClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            //
            Log.d("Check", "Accepted");
            finish();
        }
    };

    View.OnClickListener cancelClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Log.d("Check", "Cancelled");
            dialog.dismiss();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.check_activity);
        String title = "RockShare Request";
        String json = getIntent().getExtras().getString("com.parse.Data");
        try {
            object = new JSONObject(json);
            String msg = object.getString("from") + " wants you to share music with him!";
            dialog = new Dialog(this, title, msg);
            dialog.addCancelButton("Cancel");
            dialog.setOnAcceptButtonClickListener(acceptClickListener);
            dialog.setOnCancelButtonClickListener(cancelClickListener);
            dialog.show();
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void backToMusicPlayerActivity(){
        Intent intent = new Intent(this, MusicPlayerActivity.class);
        intent.setAction(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);

    }

    private void sendAcceptMessage(){
//        SharedPreferences sharedPreferences = getSharedPreferences(Constant.MEDIA_STATE, 0);

        ParseObject msg = new ParseObject(ACC_CLASS);
        msg.put("from", ParseUser.getCurrentUser().getString("url"));
        if(object == null){
            Log.d(TAG, "null json object");
            return;
        }
        try {
            msg.put("to", object.getString("url"));
            msg.saveInBackground();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RockShareServerHandler rockShareServerHandler = ((BeanConnectionApplication)getApplicationContext()).getRockShareServerHandler();


    }
}
