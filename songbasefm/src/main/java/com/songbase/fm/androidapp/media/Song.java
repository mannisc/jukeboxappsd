package com.songbase.fm.androidapp.media;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.widget.ImageView;

import com.songbase.fm.androidapp.MainActivity;
import com.songbase.fm.androidapp.R;
import com.songbase.fm.androidapp.list.ListAdapter;
import com.songbase.fm.androidapp.list.MainListElement;
import com.songbase.fm.androidapp.misc.DownloadImageTask;
import com.songbase.fm.androidapp.mymusic.MyMusicController;

import java.util.ArrayList;
import java.util.List;

/**
 * Must be GSON serializable
 */
public class Song extends MainListElement {

    public String gid;
    public String name;
    private String artist;

    public transient String album = null;

    public String playlistgid;

    public boolean isVideoUrlLoaded = false;
    public boolean isBuffered;
    public boolean isConverted;

    // Resource
    private String videoURL;

    private transient Drawable icon;

    public ListAdapter.ListLayout getListLayout() {
        return ListAdapter.ListLayout.NAMEINFO;
    }


    public Song(String gid, String name, boolean isBuffered, boolean isConverted, String artist, String playlistgid) {
        this.gid = gid;
        this.name = name;
        this.artist = artist;
        this.isConverted = isConverted;
        this.isBuffered = isBuffered;
        this.playlistgid = playlistgid;


        this.videoURL = null;


        int imageResource = MainActivity.instance.getResources().getIdentifier(
                "music", "drawable", MainActivity.instance.getPackageName());
        this.icon =  new BitmapDrawable(MainActivity.instance.getResources(),BitmapFactory.decodeResource(
                MainActivity.instance.getResources(), imageResource));
    }



    public static void loadIcon(String url, List<Song>  songs){

        Log.e("URL",url);
        new DownloadImageTask(songs)
                .execute(url);
    }



    public Song(String name, String artist) {
        this(MyMusicController.getNewID(), name, false, false, artist, "");
    }

    public String getArtistName() {
        return artist;
    }

    public void setIcon(Bitmap icon) {
        this.icon =  new BitmapDrawable(MainActivity.instance.getResources(),icon);
    }
    public Drawable getIcon() {
        return this.icon;
    }

    public String getName() {
        return name;
    }

    public String getVideoUrl() {
        return videoURL;
    }

    public void setVideoUrl(String videoURL) {
        this.videoURL = videoURL;
        this.isVideoUrlLoaded = (this.videoURL != null);
    }

    /**
     * Get Info to Songs displayed in List
     */
    public String getInfo() {
        if (!artist.equals("")) {
            if (album!=null&&!album.equals(""))
                return artist + " - " + album;
            else
                return artist;
        }
        return "";

    }

    public void setName(String name) {
        this.name = name;
    }


    public String getDisplayName() {

        if (!artist.equals(""))
            return name + " - " + artist;
        else
            return name;
    }


    public String getPlaylistGid() {
        return playlistgid;
    }

}