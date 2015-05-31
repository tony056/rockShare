package com.example.michael.rockshare;

/**
 * Created by michael on 5/29/15.
 */
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class earPhoneList extends Activity {
    /** Called when the activity is first created. */

    private Button toMenuList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ear_phone_list);
        toMenuList = (Button)findViewById(R.id.toMenuList);
    }
    public void onClickButton(View view) {
        Intent intent = new Intent();
        intent.setClass(earPhoneList.this, menuList.class);
        startActivity(intent);
        earPhoneList.this.finish();
    }
}