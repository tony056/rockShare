package com.example.tungying_chao.rockshare;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

/**
 * Created by tungying-chao on 6/3/15.
 */
public class MusicPlayerActivity extends Activity {

    private static final String TAG = "MusicPlayerActivity";

    private ImageView playAndPauseImageView;
    private ImageView.OnClickListener mClickListener = new ImageView.OnClickListener(){

        @Override
        public void onClick(View v) {
            if(v.getId() == R.id.playAndPause){
                playAndPauseImageView.setImageResource(R.drawable.pause);
            }

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.music_player_activity);
        playAndPauseImageView = (ImageView)findViewById(R.id.playAndPause);
        playAndPauseImageView.setOnClickListener(mClickListener);
    }

    @Override
    public void onPause(){
        super.onPause();
    }

    @Override
    public void onResume(){
        super.onResume();

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
