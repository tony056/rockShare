package com.example.tungying_chao.utilities;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.tungying_chao.rockshare.R;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by tungying-chao on 6/2/15.
 */
public class MusicItemAdapter extends BaseAdapter {
    private static final String TAG = "MusicItemAdapter";
//    private static final String SD_PATH = new String("/sdcard");
    private LayoutInflater mLayoutInflater;


    private Context context;


    private List<SongInfo> songInfoList = new ArrayList<SongInfo>();

    public MusicItemAdapter(Context context){
        mLayoutInflater = LayoutInflater.from(context);
        this.context = context;
        SongManager songManager = new SongManager(this.context);
        songInfoList = songManager.getSongsList();
    }

    @Override
    public int getCount() {
        return songInfoList.size();
    }

    @Override
    public SongInfo getItem(int position) {
        return songInfoList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return songInfoList.indexOf(getItem(position));
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Log.d(TAG, "" + songInfoList.size());
        convertView = mLayoutInflater.inflate(R.layout.music_list_item, null);
        TextView songNameTextView = (TextView)convertView.findViewById(R.id.songName);
        TextView songDurationTextView = (TextView)convertView.findViewById(R.id.songDuration);
        TextView songInfoTextView = (TextView)convertView.findViewById(R.id.authorAndAlbum);

        if(songInfoList.size() > position){
            SongInfo info = songInfoList.get(position);
            songNameTextView.setText(info.getName().toString());
            songDurationTextView.setText(info.getDuration());
            String author = info.getAuthor();
            Log.d(TAG, "AUTHOR:" + author);
            if(author.equals("<unknown>"))
                songInfoTextView.setText(Html.fromHtml("<b>" + "Unknown" + "</b>" + " - " + info.getAlbum()));
            else
                songInfoTextView.setText(Html.fromHtml("<b>" + author + "</b>" + " - " + info.getAlbum()));
        }

        return convertView;
    }


}
