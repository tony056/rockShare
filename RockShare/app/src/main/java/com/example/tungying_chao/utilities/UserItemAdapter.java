package com.example.tungying_chao.utilities;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.tungying_chao.rockshare.R;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tungying-chao on 6/7/15.
 */
public class UserItemAdapter extends BaseAdapter {

    private static final String TAG = "UserItemAdapter";
    private static final String NAME = "username";
    private static final String URL = "url";
    private static final String WIFI = "wifiName";
    private static final String SONG = "song";
    private static final String STATE = "state";
    private static final String ACCEPT_STATE = "accept_state";
    private static final String OFFSET = "offset";
    private static final String DEFAULT_PORT = "5566";
    private LayoutInflater mLayoutInflater;
    List<ParseUser> userLists = new ArrayList<ParseUser>();

    public UserItemAdapter(Context context, List<ParseUser> users){
        mLayoutInflater = LayoutInflater.from(context);
        this.userLists = users;
    }

    @Override
    public int getCount() {
        return userLists.size();
    }

    @Override
    public ParseObject getItem(int position) {
        return userLists.get(position);
    }

    @Override
    public long getItemId(int position) {
        return userLists.indexOf(getItem(position));
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = mLayoutInflater.inflate(R.layout.user_list_item, null);
        TextView userTextView = (TextView)convertView.findViewById(R.id.userName);
        TextView songTextView = (TextView) convertView.findViewById(R.id.songName);
//        TextView stateTextView = (TextView) convertView.findViewById(R.id.userState);

        ParseUser parseObject = userLists.get(position);
        userTextView.setText(parseObject.getString(NAME));
        String song = "Not Listening";
        if(parseObject.getString(Constant.SONG) != null) {
            if (parseObject.getString(Constant.SONG).length() > 0)
                song = parseObject.getString(Constant.SONG);
        }
            songTextView.setText(song);
        int state = parseObject.getInt(Constant.STATE);
        String stateStr = "";
        switch (state){
            case 0:
                stateStr = "Idle";
                break;
            case 1:
                stateStr = "Sharing";
                break;
            case 4:
                stateStr = "Connected";
                break;

        }
//        stateTextView.setText(stateStr);
        return convertView;
    }
}
