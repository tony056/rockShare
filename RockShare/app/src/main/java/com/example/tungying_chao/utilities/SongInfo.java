package com.example.tungying_chao.utilities;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by tungying-chao on 6/3/15.
 */
public class SongInfo {
    private String name = "";
    private String author = "";
    private long duration = 0;
    private String album = "";

    public SongInfo(String name, String author, long duration, String album){
        this.name = name;
        this.author = author;
        this.duration = duration;
        this.album = album;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getDuration() {
        StringBuffer buf = new StringBuffer();

        int hours = (int) (duration / (1000 * 60 * 60));
        int minutes = (int) ((duration % (1000 * 60 * 60)) / (1000 * 60));
        int seconds = (int) (((duration % (1000 * 60 * 60)) % (1000 * 60)) / 1000);

        buf.append(String.format("%02d", hours))
                .append(":")
                .append(String.format("%02d", minutes))
                .append(":")
                .append(String.format("%02d", seconds));

        return buf.toString();
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }
}
