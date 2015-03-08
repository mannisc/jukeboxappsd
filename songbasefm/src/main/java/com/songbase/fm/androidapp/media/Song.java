package com.songbase.fm.androidapp.media;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.songbase.fm.androidapp.MainActivity;
import com.songbase.fm.androidapp.list.ListAdapter;
import com.songbase.fm.androidapp.list.MainListElement;
import com.songbase.fm.androidapp.mymusic.MyMusicController;

/**
 * Must be GSON serializable
 */
public class Song extends MainListElement {

    public String gid;
    public String name;
    private String artist;

    public String playlistgid;

    public boolean isVideoUrlLoaded = false;
    public boolean isBuffered;
    public boolean isConverted;

    // Resource
    private String videoURL;

    private transient Bitmap icon;

    public ListAdapter.ListLayout getListLayout() {
        return ListAdapter.ListLayout.NAMEINFO;
    }



    public Song(String gid,String name, boolean isBuffered, boolean isConverted, String artist, String playlistgid) {
        this.gid = gid;
        this.name = name;
        this.artist = artist;
        this.isConverted = isConverted;
        this.isBuffered = isBuffered;
        this.playlistgid = playlistgid;


        this.videoURL = "http://www.dailymotion.com/video/x25ud6r_calvin-harris-ft-john-newman-blame-official-video-hd-720p_music";



        int imageResource = MainActivity.instance.getResources().getIdentifier(
                "playlist", "drawable", MainActivity.instance.getPackageName());
        this.icon = BitmapFactory.decodeResource(
                MainActivity.instance.getResources(), imageResource);
    }


    public Song(String name, String artist) {
        this(MyMusicController.getNewID(),name, false, false,  artist , "");
    }

    public String getArtistName() {
        return artist;
    }

    public Bitmap getIcon() {

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
        return "TODO Artist";
    }

    public void setName(String name) {
        this.name = name;
    }


    public String getDisplayName() {

        if (artist != "")
            return artist + " - " + name;
        else
            return name;
    }


    public String getPlaylistGid(){
        return playlistgid;
    }

}