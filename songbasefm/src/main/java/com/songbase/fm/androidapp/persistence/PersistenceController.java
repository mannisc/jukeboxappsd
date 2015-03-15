package com.songbase.fm.androidapp.persistence;

import android.app.Activity;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.gson.Gson;
import com.songbase.fm.androidapp.list.MainListElement;
import com.songbase.fm.androidapp.media.PlaylistListElement;
import com.songbase.fm.androidapp.media.Song;
import com.songbase.fm.androidapp.media.SongAction;
import com.songbase.fm.androidapp.media.SongListElement;
import com.songbase.fm.androidapp.misc.Utils;
import com.songbase.fm.androidapp.mymusic.MyMusicController;
import com.songbase.fm.androidapp.popular.PopularController;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Manfred on 25.02.2015.
 */
public class PersistenceController {

    // Main Activity
    public Activity activity;

    public static PersistenceController instance;

    public static final String PERSISTENCEDATA_NAME = "SongbaseData";

    public static final Gson gson = new Gson();

    public static SharedPreferences data;

    public String playlistsJSON = "";

    public PersistenceController(Activity activity) {
        instance = this;
        this.activity = activity;
        data = activity.getSharedPreferences(PERSISTENCEDATA_NAME, 0);

    }


    public void loadStoredOfflineData() {

        playlistsJSON = data.getString("playlists", "{}");

        //Buffered
        //playlistsJSON = "{\"items\": [{ \"name\": \"Test Playlist\",\"data\": [{\"name\": \"Love Me Like You Do\", \"isVideoUrlLoaded\":\"true\", \"isBuffered\":\"true\", \"isConverted\":\"true\",  \"videoURL\":\"true\" , \"artist\": {\"name\": \"Ellie Goulding\"}}]}]}";

        //Unbuffered
       /* playlistsJSON = "{\"items\": [{ \"name\": \"Test Playlist\",  \"gid\": \"1234567\",     \"data\": [" +
                "{\"name\": \"Love Me Like You Do\", \"playlistgid\": \"1234567\", \"isVideoUrlLoaded\":\"true\", \"isBuffered\":\"false\", \"isConverted\":\"false\",  \"videoURL\":\"true\" , \"artist\": {\"name\": \"Ellie Goulding\"}}," +
                "{\"name\": \"Sonne\", \"playlistgid\": \"1234567\", \"isVideoUrlLoaded\":\"true\", \"isBuffered\":\"false\", \"isConverted\":\"false\",  \"videoURL\":\"true\" , \"artist\": {\"name\": \"Rammstein\"}}," +
                "{\"name\": \"Love Me Like You Do\", \"playlistgid\": \"1234567\", \"isVideoUrlLoaded\":\"true\", \"isBuffered\":\"false\", \"isConverted\":\"false\",  \"videoURL\":\"true\" , \"artist\": {\"name\": \"Ellie Goulding\"}}," +
                "{\"name\": \"Sonne\", \"playlistgid\": \"1234567\", \"isVideoUrlLoaded\":\"true\", \"isBuffered\":\"false\", \"isConverted\":\"false\",  \"videoURL\":\"true\" , \"artist\": {\"name\": \"Rammstein\"}}," +
                "{\"name\": \"Love Me Like You Do\", \"playlistgid\": \"1234567\", \"isVideoUrlLoaded\":\"true\", \"isBuffered\":\"false\", \"isConverted\":\"false\",  \"videoURL\":\"true\" , \"artist\": {\"name\": \"Ellie Goulding\"}}," +
                "{\"name\": \"Sonne\", \"playlistgid\": \"1234567\", \"isVideoUrlLoaded\":\"true\", \"isBuffered\":\"false\", \"isConverted\":\"false\",  \"videoURL\":\"true\" , \"artist\": {\"name\": \"Rammstein\"}}" +

                "]}]}";
                */


        try {

            JSONObject json = new JSONObject(playlistsJSON);

            List<PlaylistListElement> list = MyMusicController
                    .getPlaylistsFromJSON(json);

            MyMusicController.instance.setPlaylistList(list);


            List<MainListElement> songs = loadPlayedSong();
            MyMusicController.instance.setPlayedSongs(songs);


        } catch (JSONException e) {
            e.printStackTrace();
        }


        saveOfflineData();//TODO remove

        //Load Popular Songs
        PersistenceController.instance.loadPopularSongs();
    }


    public List<MainListElement> loadPlayedSong() {
        String playedSongsJSON = data.getString("playedsongs", "");
        return loadPlayedSong(playedSongsJSON);
    }

    public List<MainListElement> loadPlayedSong(String playedSongsJSON) {

        List<MainListElement> songs = new ArrayList<MainListElement>();

        if (playedSongsJSON != null && !playedSongsJSON.equals("")) {

            List<Song> songList = MyMusicController.getSongsFromJSON(playedSongsJSON);

            for (Song song : songList) {
                songs.add(new SongListElement(song, new SongAction(
                        song)));
            }

        }

        return songs;

    }


    public void saveOfflineData() {

        playlistsJSON = MyMusicController.instance.getPlaylistsAsJSON();

        Log.e("SAVE JSON", playlistsJSON);

        //TODO Remove
        //playlistsJSON = "{\"items\": [{ \"name\": \"Test Playlist\",\"data\": [{\"name\": \"Love Me Like You Do\", \"isVideoUrlLoaded\":\"true\",   \"videoURL\":\"true\" , \"artist\": {\"name\": \"Ellie Goulding\"}}]}]}";


        SharedPreferences.Editor editor = data.edit();
        editor.putString("playlists", playlistsJSON);
        editor.commit();

    }

    public void loadPopularSongs() {

        String songsJSON = data.getString("popularsongs", "");
        String songsLastUpdate = data.getString("popularsongslastupdate", "");

        if (!songsJSON.equals("") && !songsLastUpdate.equals("")){
            PopularController.instance.lastUpdate = Long.parseLong(songsLastUpdate);
            PopularController.instance.setPopularSongsFromJSON(songsJSON);
        }
       else{
            PopularController.instance.getPopularSongs( data.getString("activeSong", "").equals(""));


        }

    }


    public void savePopularSongs(Long lastUpdate, List<MainListElement> songs) {

        String popularSongs = MyMusicController.instance.getSongsAsJSON(songs);

        Log.e("SAVE JSON", playlistsJSON);

        SharedPreferences.Editor editor = data.edit();
        editor.putString("popularsongs", popularSongs);
        editor.putString("popularsongslastupdate", lastUpdate.toString());

        editor.commit();

    }


}
