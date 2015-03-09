package com.songbase.fm.androidapp.persistence;

import android.app.Activity;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.gson.Gson;
import com.songbase.fm.androidapp.media.Playlist;
import com.songbase.fm.androidapp.media.PlaylistListElement;
import com.songbase.fm.androidapp.misc.Utils;
import com.songbase.fm.androidapp.mymusic.MyMusicController;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by Manfred on 25.02.2015.
 */
public class PersistenceController {

    // Main Activity
    public Activity activity;

    public static PersistenceController instance;

    public static final String PERSISTENCEDATA_NAME = "SongbaseData";


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
        playlistsJSON = "{\"items\": [{ \"name\": \"Test Playlist\",  \"gid\": \"1234567\",     \"data\": [" +
                "{\"name\": \"Love Me Like You Do\", \"playlistgid\": \"1234567\", \"isVideoUrlLoaded\":\"true\", \"isBuffered\":\"false\", \"isConverted\":\"false\",  \"videoURL\":\"true\" , \"artist\": {\"name\": \"Ellie Goulding\"}}," +
                "{\"name\": \"Sonne\", \"playlistgid\": \"1234567\", \"isVideoUrlLoaded\":\"true\", \"isBuffered\":\"false\", \"isConverted\":\"false\",  \"videoURL\":\"true\" , \"artist\": {\"name\": \"Rammstein\"}}," +
                "{\"name\": \"Love Me Like You Do\", \"playlistgid\": \"1234567\", \"isVideoUrlLoaded\":\"true\", \"isBuffered\":\"false\", \"isConverted\":\"false\",  \"videoURL\":\"true\" , \"artist\": {\"name\": \"Ellie Goulding\"}}," +
                "{\"name\": \"Sonne\", \"playlistgid\": \"1234567\", \"isVideoUrlLoaded\":\"true\", \"isBuffered\":\"false\", \"isConverted\":\"false\",  \"videoURL\":\"true\" , \"artist\": {\"name\": \"Rammstein\"}}," +
                "{\"name\": \"Love Me Like You Do\", \"playlistgid\": \"1234567\", \"isVideoUrlLoaded\":\"true\", \"isBuffered\":\"false\", \"isConverted\":\"false\",  \"videoURL\":\"true\" , \"artist\": {\"name\": \"Ellie Goulding\"}}," +
                "{\"name\": \"Sonne\", \"playlistgid\": \"1234567\", \"isVideoUrlLoaded\":\"true\", \"isBuffered\":\"false\", \"isConverted\":\"false\",  \"videoURL\":\"true\" , \"artist\": {\"name\": \"Rammstein\"}}" +

                "]}]}";

        try {

            JSONObject json = new JSONObject(playlistsJSON);

            List<PlaylistListElement> list = MyMusicController
                    .parsePlaylistJSON(json);
            MyMusicController.instance.setPlaylistList(list);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        saveOfflineData();//TODO remove

    }


    public void saveOfflineData() {

        playlistsJSON = MyMusicController.instance.getPlaylistsAsJSON();

        Log.e("SAVE JSON",playlistsJSON);

        //TODO Remove
        //playlistsJSON = "{\"items\": [{ \"name\": \"Test Playlist\",\"data\": [{\"name\": \"Love Me Like You Do\", \"isVideoUrlLoaded\":\"true\",   \"videoURL\":\"true\" , \"artist\": {\"name\": \"Ellie Goulding\"}}]}]}";

        SharedPreferences.Editor editor = data.edit();
        editor.putString("playlists", playlistsJSON);
        editor.commit();

    }


}
