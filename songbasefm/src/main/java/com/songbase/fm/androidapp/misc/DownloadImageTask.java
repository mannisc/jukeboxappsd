package com.songbase.fm.androidapp.misc;

import android.app.Notification;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import com.songbase.fm.androidapp.MainActivity;
import com.songbase.fm.androidapp.media.Song;
import com.songbase.fm.androidapp.playing.PlayController;

import java.io.InputStream;
import java.util.List;

/**
 * Created by Manfred on 09.03.2015.
 */

public class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
    List<Song> songs;
    DownloadCallback callback = null;

    public DownloadImageTask(List<Song> songs) {
        this.songs = songs;
    }


    public DownloadImageTask(DownloadCallback callback,List<Song> songs) {
        this.songs = songs;
        this.callback = callback;
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

        for (Song song : songs) {
            song.loadingIcon = false;
            if (result != null) {
                song.setIcon(result);
                //Update Playing cover
                Log.e("SONGNAME",song.getDisplayName());
                if (PlayController.instance.activeSong.gid.equals(song.gid))
                    PlayController.instance.updateSongCover();
            }
        }


        if (result != null)
            MainActivity.instance.listController.refreshList();


        if(callback!=null)
            callback.callback(result);
        
        
    }

    public static class DownloadCallback {

        public void callback(Bitmap result) {
        }
    }
}