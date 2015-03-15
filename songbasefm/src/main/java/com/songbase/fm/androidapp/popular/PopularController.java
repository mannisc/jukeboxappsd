package com.songbase.fm.androidapp.popular;

import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.google.gson.Gson;
import com.songbase.fm.androidapp.MainActivity;
import com.songbase.fm.androidapp.account.AccountController;
import com.songbase.fm.androidapp.authentication.AuthController;
import com.songbase.fm.androidapp.authentication.RSAUtils;
import com.songbase.fm.androidapp.list.MainListElement;
import com.songbase.fm.androidapp.media.Playlist;
import com.songbase.fm.androidapp.media.PlaylistListElement;
import com.songbase.fm.androidapp.media.Song;
import com.songbase.fm.androidapp.media.SongAction;
import com.songbase.fm.androidapp.media.SongListElement;
import com.songbase.fm.androidapp.mymusic.MyMusicController;
import com.songbase.fm.androidapp.persistence.PersistenceController;
import com.songbase.fm.androidapp.playing.PlayController;
import com.songbase.fm.androidapp.settings.Settings;
import com.songbase.fm.androidapp.ui.UIController;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Manfred on 14.03.2015.
 */
public class PopularController {

    public List<MainListElement> list = new ArrayList<MainListElement>();

    public Playlist playlist;

    public long lastUpdate = 0;

    public static PopularController instance;


    public AQuery aQuery;
    public static Gson gson = new Gson();

    public PopularController(List<MainListElement> list) {
        PopularController.instance = this;
        this.list = list;
        this.playlist =  new Playlist("Popular Songs", MyMusicController.popularSongsPlaylistGid,this.list);


        int imageResource = MainActivity.instance.getResources().getIdentifier(
                "musictrans", "drawable", MainActivity.instance.getPackageName());
        this.playlist.icon = new BitmapDrawable(MainActivity.instance.getResources(), BitmapFactory.decodeResource(
                MainActivity.instance.getResources(), imageResource));


        aQuery = MainActivity.instance.aQuery;


    }


    public void getPopularSongs(final boolean setActiveSong) {

        Log.e("LAST UPDATE", Long.toString((long) ((System.currentTimeMillis() - lastUpdate) / 60.0f / 1000.0f)));
        if (list.size() == 0 || System.currentTimeMillis() - lastUpdate > 24 * 60 * 60 * 1000) {


            String url = Settings.pageURL + "/public/js/generatedData.js";


            Log.e("getPopularSongs", url);//TODO remove

            AjaxCallback<String> cb = new AjaxCallback<String>() {

                @Override
                public void callback(String url, String html, AjaxStatus status) {

                    if (html != null) {
                        Log.e("callback", html);


                        final Pattern pattern = Pattern.compile("generatedData.charts = (.+?);");
                        final Matcher matcher = pattern.matcher(html);
                        if (matcher.find()) {

                            try {

                                String jsonString = matcher.group(1);

                                Log.e("matcher", jsonString);
                                setPopularSongsFromJSON(jsonString);

                                if (list.size() > 0) {

                                    //Set popular song as active Song on startup
                                    if (setActiveSong) {
                                        PlayController.instance.setPlayingSongInfo(((SongListElement) (list.get(0))).getSong());
                                    }

                                    lastUpdate = System.currentTimeMillis();
                                    PersistenceController.instance.savePopularSongs(lastUpdate, list);
                                }


                            } catch (IllegalStateException e) {
                                e.printStackTrace();
                            }
                        }


                    } else

                    {
                        Toast.makeText(aQuery.getContext(),
                                "Error:" + status.getCode(), Toast.LENGTH_LONG)
                                .show();
                    }
                }

            };

            cb = cb.encoding("UTF-8");
            cb.header("Referer", "songbase.fm");
            aQuery.ajax(url, String.class, -1, cb);

        }
    }

    public void setPopularSongsFromJSON(String jsonString) {
        List<Song> songList = MyMusicController.getSongsFromJSON(jsonString);

        Log.e("Songs", Integer.toString(songList.size()));

        List<MainListElement> songs = new ArrayList<MainListElement>();

        for (Song song : songList) {
            song.playlistgid = MyMusicController.popularSongsPlaylistGid;
            songs.add(new SongListElement(song,
                    new SongAction(song)));

        }
        list.clear();

        if (list.size() > 100)
            list.addAll(songs.subList(0, 100));
        else
            list.addAll(songs);


        // Set list
        if (UIController.instance.isModeActive(UIController.POPULARMODE))
            MainActivity.instance.listController.setList(list);
    }


    public Playlist getPlaylist() {
        return playlist;
    }
}

