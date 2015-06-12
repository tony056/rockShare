package com.example.tungying_chao.utilities;

import java.io.File;
import java.io.FilenameFilter;

/**
 * Created by tungying-chao on 6/2/15.
 */
public class Mp3Filter implements FilenameFilter {
    private static final String format = ".mp3";

    @Override
    public boolean accept(File dir, String filename) {
        return (filename.endsWith(format));
    }
}
