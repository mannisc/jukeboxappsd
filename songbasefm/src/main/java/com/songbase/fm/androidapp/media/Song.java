package com.songbase.fm.androidapp.media;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.util.Log;

import com.songbase.fm.androidapp.MainActivity;
import com.songbase.fm.androidapp.list.ListAdapter;
import com.songbase.fm.androidapp.list.MainListElement;
import com.songbase.fm.androidapp.misc.DownloadImageTask;
import com.songbase.fm.androidapp.misc.Utils;
import com.songbase.fm.androidapp.mymusic.MyMusicController;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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

    public String imageURLJSON = null;

    public transient boolean loadingIcon = false;
    private transient Drawable icon = null;
    private transient static Drawable defaultIcon;

    public ListAdapter.ListLayout getListLayout() {
        return ListAdapter.ListLayout.NAMEINFO;
    }

    public Song(Song song) {
        this(song.gid, song.name, song.isBuffered, song.isConverted, song.artist, song.playlistgid);
        imageURLJSON = song.imageURLJSON;
        videoURL = song.videoURL;
        icon = song.icon;
    }


    public Song(String gid, String name, boolean isBuffered, boolean isConverted, String artist, String playlistgid) {
        this.gid = gid;
        this.name = name;
        this.artist = artist;
        this.isConverted = isConverted;
        this.isBuffered = isBuffered;
        this.playlistgid = playlistgid;
        this.videoURL = null;
        this.icon = null;
    }


    public static void loadIconCallback(String url, List<Song> songs, DownloadImageTask.DownloadCallback callback) {
        new DownloadImageTask(callback, songs)
                .execute(url);
    }

    public static void loadIcon(String url, List<Song> songs) {
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
        this.icon = new BitmapDrawable(MainActivity.instance.getResources(), icon);
    }

    public Drawable getIconCallback(final DownloadImageTask.DownloadCallback callback) {

        if (icon == null) {

            //Load Song Cover


            Log.e("SONG.imageURLJSON", Boolean.toString(this.imageURLJSON != null) + "  " + ((this.imageURLJSON != null) ? this.imageURLJSON : ""));


            if (this.imageURLJSON != null && !loadingIcon) {
                if (Utils.getDownloadDataLimitation() == 2) {

                    loadingIcon = true;
                    final Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {

                            try {
                                String iconURL = ((JSONObject) new JSONArray(Song.this.imageURLJSON).get(1)).getString("#text");

                                if (iconURL != null) {
                                    List<Song> songsIcon = new ArrayList<Song>();
                                    songsIcon.add(Song.this);
                                    if (callback == null)
                                        Song.loadIcon(iconURL, songsIcon);
                                    else
                                        Song.loadIconCallback(iconURL, songsIcon, callback);


                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, 150);

                }
            }

            if (Song.defaultIcon == null) {
                int imageResource = MainActivity.instance.getResources().getIdentifier(
                        "music", "drawable", MainActivity.instance.getPackageName());
                Song.defaultIcon = new BitmapDrawable(MainActivity.instance.getResources(), BitmapFactory.decodeResource(
                        MainActivity.instance.getResources(), imageResource));

            }
            return Song.defaultIcon;
        } else {
            if (callback != null)
                callback.callback(((BitmapDrawable) (this.icon)).getBitmap());

            return this.icon;
        }
    }


    public Drawable getIcon() {
        return getIconCallback(null);
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
            if (album != null && !album.equals(""))
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