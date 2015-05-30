package com.example.michael.rockshare;

/**
 * Created by michael on 5/29/15.
 */
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class musicList extends Activity {
    /**
     * Called when the activity is first created.
     */
    private ListView listView;
    private ArrayAdapter listAdapter;
    private Button goBack;
    private String[] songList = {"Would Yuo Go With Me", "Wagon Whee!", "Chillin' it"};
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.music_list);

        listView = (android.widget.ListView)findViewById(R.id.listView);
        goBack = (Button) findViewById(R.id.goBack);

        listAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, songList);
        listView.setAdapter(listAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView parent, View view, int position, long id){

                SharedPreferences.Editor editor = getApplicationContext().getSharedPreferences("My_MUSIC", MODE_PRIVATE).edit();
                editor.putString("music", songList[position]);
                editor.commit();

                Intent intent = new Intent();
                intent.setClass(musicList.this, musicPlayer.class);
                startActivity(intent);
                musicList.this.finish();
            }
        });
    }

    public void goBack(View view) {
        Intent intent = new Intent();
        intent.setClass(musicList.this, menuList.class);
        startActivity(intent);
        musicList.this.finish();
    }

}