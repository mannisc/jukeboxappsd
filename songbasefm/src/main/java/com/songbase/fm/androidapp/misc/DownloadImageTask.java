package com.songbase.fm.androidapp.misc;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import com.songbase.fm.androidapp.MainActivity;
import com.songbase.fm.androidapp.media.Song;

import java.io.InputStream;
import java.util.List;

/**
 * Created by Manfred on 09.03.2015.
 */

public class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
    List<Song>  songs ;

    public DownloadImageTask(List<Song> songs) {
        this.songs = songs;
    }

    protected Bitmap doInBackground(String... urls) {
        String url = urls[0];
        Bitmap mIcon11 = null;
        try {
            InputStream in = new java.net.URL(url).openStream();
            mIcon11 = BitmapFactory.decodeStream(in);
        } catch (Exception e) {
            Log.e("Error", e.getMessage());
            e.printStackTrace();
        }
        return mIcon11;
    }

    protected void onPostExecute(Bitmap result) {

        if (result != null) {
            for(Song song:songs){
                song.setIcon(result);
            }
            MainActivity.instance.listController.refreshList();
        }

    }
}