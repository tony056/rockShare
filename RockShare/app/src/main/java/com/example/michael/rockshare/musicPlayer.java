package com.example.michael.rockshare;

/**
 * Created by michael on 5/29/15.
 */
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

public class musicPlayer extends Activity {
    /**
     * Called when the activity is first created.
     */
    private TextView chosenSong;
    private String song;
    private ImageButton lastSong;
    private ImageButton playPause;
    private ImageButton nextSong;
    private Button goBack;
    private int check;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.music_player);
        chosenSong = (TextView) findViewById(R.id.chosenSong);
        lastSong = (ImageButton) findViewById(R.id.lastSong);
        playPause = (ImageButton) findViewById(R.id.playPause);
        nextSong = (ImageButton) findViewById(R.id.nextSong);
        playPause.setBackgroundResource(R.drawable.pause);
        lastSong.setBackgroundResource(R.drawable.last_song);
        nextSong.setBackgroundResource(R.drawable.next_song);
        goBack = (Button) findViewById(R.id.goBack);
        check = 1;

        SharedPreferences prefs = getApplicationContext().getSharedPreferences("My_MUSIC", MODE_PRIVATE);
        song = prefs.getString("music", "No song chosen");
        chosenSong.setText("Now playing ..."+song);
    }

    public void goBack(View view) {
        Intent intent = new Intent();
        intent.setClass(musicPlayer.this, musicList.class);
        startActivity(intent);
        musicPlayer.this.finish();
    }

    public void clickLastSong(View view) {
        // action to do when clicking last song button
    }

    public void clickPlayPause(View view) {
        if(check == 1) {
            playPause.setBackgroundResource(R.drawable.play);
            check = 0;
        }
        else {
            playPause.setBackgroundResource(R.drawable.pause);
            check = 1;
        }

    }

    public void clickNextSong(View view){
        // action to do when clicking next song button
    }
}