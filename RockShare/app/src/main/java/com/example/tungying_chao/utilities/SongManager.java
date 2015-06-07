package com.example.tungying_chao.utilities;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by tungying-chao on 6/7/15.
 */
public class SongManager {
    private static final String TAG = "SongManager";
    Context context;
    private Uri contentURi = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
    private Cursor mCursor;
    private int titleIndex;
    private int authorIndex;
    private int albumIndex;
    private int durationIndex;
    private int pathIndex;
    private String[] songInfoColumn = new String[] {
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.DATA
    };
    private List<SongInfo> songInfoList = new ArrayList<SongInfo>();

    public SongManager(Context context) {
        this.context = context;
        mCursor = context.getContentResolver().query(contentURi, songInfoColumn, null, null, MediaStore.Audio.Media.DEFAULT_SORT_ORDER);
        titleIndex = mCursor.getColumnIndexOrThrow(songInfoColumn[0]);
        albumIndex = mCursor.getColumnIndexOrThrow(songInfoColumn[1]);
        durationIndex = mCursor.getColumnIndexOrThrow(songInfoColumn[2]);
        authorIndex = mCursor.getColumnIndexOrThrow(songInfoColumn[3]);
        pathIndex = mCursor.getColumnIndexOrThrow(songInfoColumn[4]);
        getMp3FilesInfo();
    }

    private void getMp3FilesInfo(){
        Log.d(TAG, "" + mCursor.getCount());
        if(mCursor.getCount() > 0){
            while(mCursor.moveToNext()){
                Log.d(TAG, "" + mCursor.getPosition());
                String title = mCursor.getString(titleIndex);
                String author = mCursor.getString(authorIndex);
                long duration = mCursor.getLong(durationIndex);
                String album = mCursor.getString(albumIndex);
                String path = mCursor.getString(pathIndex);
                SongInfo info = new SongInfo(title, author, duration, album, path);
                songInfoList.add(info);
            }
            mCursor.close();
        }
    }

    public List<SongInfo> getSongsList(){
        return songInfoList;
    }
}
