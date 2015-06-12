package com.example.tungying_chao.utilities;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.tungying_chao.rockshare.R;

import java.util.List;

import nl.littlerobots.bean.Bean;
import nl.littlerobots.bean.message.Callback;

/**
 * Created by tungying-chao on 6/1/15.
 */
public class BeanItemAdapter extends BaseAdapter {
    private LayoutInflater mLayoutInflater;
    private List<Bean> beans;

    public BeanItemAdapter(Context context, List<Bean> beanList){
        mLayoutInflater = LayoutInflater.from(context);
        this.beans = beanList;
    }

    @Override
    public int getCount() {
        return beans.size();
    }

    @Override
    public Object getItem(int position) {
        return beans.get(position);
    }

    @Override
    public long getItemId(int position) {
        return beans.indexOf(getItem(position));
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = mLayoutInflater.inflate(R.layout.bean_list_item, null);
        TextView beanNameTextView = (TextView)convertView.findViewById(R.id.beanName);

        if(beans.size() > 0) {
            String beanName = beans.get(position).getDevice().getName().toString();
            beanNameTextView.setText(beanName);
        }

        return convertView;
    }
}
