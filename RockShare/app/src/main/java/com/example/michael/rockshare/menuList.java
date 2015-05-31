package com.example.michael.rockshare;

/**
 * Created by michael on 5/29/15.
 */
import android.app.Activity;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

public class menuList extends Activity {
    /** Called when the activity is first created. */

    private ImageButton discover;
    private ImageButton musicPlayer;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu_list);
        discover = (ImageButton)findViewById(R.id.discover);
        musicPlayer = (ImageButton)findViewById(R.id.musicPlayer);
    }

    public void onClickDiscover(View view) {
        Intent intent = new Intent();
        intent.setClass(menuList.this, userList.class);
        startActivity(intent);
        menuList.this.finish();
    }
    public void onClickMusic(View view) {
        Intent intent = new Intent();
        intent.setClass(menuList.this, musicList.class);
        startActivity(intent);
        menuList.this.finish();
    }

}