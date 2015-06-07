package com.example.tungying_chao.rockshare;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.gc.materialdesign.widgets.Dialog;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by tungying-chao on 6/7/15.
 */
public class CheckActivity extends Activity {

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
            JSONObject object = new JSONObject(json);
            String msg = object.getString("from");
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
}
