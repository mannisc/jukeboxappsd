package com.songbase.fm.androidapp.buffering.grabstream;

import android.util.Log;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.net.URL;

/**
 * Created by Manfred on 27.02.2015.
 */
public class GrabSocket extends java.net.Socket {

   private String url;

    public GrabSocket(String url){
        super();
        this.url = url;
    }




    public FileDescriptor getFileDescriptor$(){
        Log.e("XXXXXXXXXXXX","GETFILEDESCITPOR$");

        InputStream is = null;
        try {
            is = new URL(this.url).openStream();

        } catch (IOException e) {
            e.printStackTrace();
        }

        File file = new File("/data/data/com.songbase.fm.androidapp/app_buffer/621996868.mp3");
        FileInputStream fos = null;
        try {
            fos = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        // remember th 'fos' reference somewhere for later closing it
        try {
            return fos.getFD();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }


}
