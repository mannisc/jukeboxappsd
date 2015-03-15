package com.songbase.fm.androidapp.mymusic;

import android.util.Log;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.androidquery.AQuery;
import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import com.songbase.fm.androidapp.MainActivity;
import com.songbase.fm.androidapp.list.MainListElement;
import com.songbase.fm.androidapp.media.Playlist;
import com.songbase.fm.androidapp.media.PlaylistAction;
import com.songbase.fm.androidapp.media.PlaylistAllSongs;
import com.songbase.fm.androidapp.media.PlaylistListElement;
import com.songbase.fm.androidapp.media.Song;
import com.songbase.fm.androidapp.media.SongAction;
import com.songbase.fm.androidapp.media.SongListElement;
import com.songbase.fm.androidapp.misc.Utils;
import com.songbase.fm.androidapp.playing.PlayController;
import com.songbase.fm.androidapp.ui.UIController;
import com.songbase.fm.androidapp.ui.viewmode.MyMusicMode;

public class MyMusicController {


    public List<MainListElement> list = new ArrayList<MainListElement>();

    public List<MainListElement> defaultListElements = new ArrayList<MainListElement>();


    public static MyMusicController instance;

    public AQuery aQuery;
    public static Gson gson = new Gson();


    public boolean isSubModeActive = false;

    public Playlist loadedPlaylist;//displayed Playlist if submode is active

    public static long counterGlobalId = 0;

    public static String allSongsPlaylistGid = "id_0";
    public static String playedSongsPlaylistGid = "id_1";
    public static String popularSongsPlaylistGid = "id_2";

    public Playlist playedSongsPlaylist;


    public MyMusicController(List<MainListElement> list) {
        MyMusicController.instance = this;
        this.list = list;
        aQuery = MainActivity.instance.aQuery;

        playedSongsPlaylist = new Playlist("Played Songs", MyMusicController.playedSongsPlaylistGid, new ArrayList<MainListElement>());
        this.defaultListElements.add(new PlaylistListElement(playedSongsPlaylist,
                new PlaylistAction(playedSongsPlaylist)));
    }


    public void setPlaylistList(List<PlaylistListElement> list) {

        this.list.clear();

        this.list.addAll(list);
        this.list.addAll(1, this.defaultListElements);

        //Set active Playlist after Paylists got loaded
        if (PlayController.instance.activeSong != null)
            PlayController.instance.setActivePlaylistByGid(PlayController.instance.activeSong.getPlaylistGid());

        if (UIController.instance.isModeActive(UIController.MYMUSICMODE))
            MainActivity.instance.listController.refreshList();
    }


    public String getPlaylistsAsJSON() {

        String playlistsJSON = "{\"items\": [";

        for (MainListElement playlistElement : list) {

            Playlist playlist = ((PlaylistListElement) playlistElement).getPlaylist();

            if (!playlist.getGid().equals(MyMusicController.allSongsPlaylistGid)) {

                String playlistJSON = "{ \"name\": \"" + playlist.getName() + "\",\"gid\": \"" + playlist.getGid() + "\",\"data\": ";

                List<MainListElement> playlistSongs = playlist.getList();

                playlistJSON = playlistJSON + getSongsAsJSON(playlistSongs)+"}";

                //insert Playlist into Playlist list
                playlistsJSON = playlistsJSON + playlistJSON;
                if (list.indexOf(playlistElement) < list.size() - 1)
                    playlistsJSON = playlistsJSON + ",";

            }

        }

        playlistsJSON = playlistsJSON + "]}";
        return playlistsJSON;

    }


    public String getSongsAsJSON( List<MainListElement> playlistSongs){

        String playlistJSON ="[";

        String songsJSON = "";
        for (MainListElement songElement : playlistSongs) {
            Song song = ((SongListElement) songElement).getSong();
            songsJSON = songsJSON + gson.toJson(song);
            if (playlistSongs.indexOf(songElement) < playlistSongs.size() - 1)
                songsJSON = songsJSON + ",";
        }

        //Insert songs into playlist
        playlistJSON = playlistJSON + songsJSON;

        playlistJSON = playlistJSON + "]";

        return playlistJSON;
    }




    public static List<PlaylistListElement> getPlaylistsFromJSON(JSONObject json) {
        List<PlaylistListElement> list = new ArrayList<PlaylistListElement>();
        List<MainListElement> allSongs = new ArrayList<MainListElement>();

        if (json != null) {

            try {

                JSONArray playlistMatches = json.getJSONArray("items");

                list.clear();
                for (int i = 0; i < playlistMatches.length(); i++) {
                    JSONObject playlistJSON = playlistMatches.getJSONObject(i);

                    JSONArray playlistSongs = playlistJSON.getJSONArray("data");

                    List<MainListElement> songs = new ArrayList<MainListElement>();

                    List<Song> songList = MyMusicController.getSongsFromJSON(playlistSongs.toString());

                    for (Song song : songList) {
                        songs.add(new SongListElement(song,
                                new SongAction(song)));

                        //Add Song to All Songs Playlist
                        Song copySong = new Song(song);
                        copySong.playlistgid = MyMusicController.allSongsPlaylistGid;
                        copySong.album = playlistJSON.getString("name");

                        allSongs.add(new SongListElement(copySong,
                                new SongAction(copySong)));
                    }


                    Playlist playlist = new Playlist(
                            playlistJSON.getString("name"), playlistJSON.getString("gid"), songs);

                    list.add(new PlaylistListElement(playlist,
                            new PlaylistAction(playlist)));

                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }


        Playlist playlist = new Playlist("All Songs", MyMusicController.allSongsPlaylistGid, allSongs);
        list.add(0, new PlaylistListElement(playlist, new PlaylistAction(playlist)));

        return list;
    }


    public static Song getSongFromJSON(String songJSON) {


        try {

            JSONObject trackJSON = new JSONObject(songJSON);

            String artist;
            try {
                JSONObject artistJSON = trackJSON
                        .getJSONObject("artist");
                artist = artistJSON.getString("name");
            } catch (JSONException e) {
                artist = trackJSON.getString("artist");
            }

            boolean isBuffered = trackJSON.has("isBuffered") && trackJSON.getBoolean("isBuffered");
            boolean isConverted = trackJSON.has("isConverted") && trackJSON.getBoolean("isConverted");

            String songGid;
            if (trackJSON.has("gid")) {
                songGid = trackJSON.getString("gid");
            } else {
                songGid = MyMusicController.getNewID();
            }

            String playlistGid;
            if (trackJSON.has("playlistgid")) {
                playlistGid = trackJSON.getString("playlistgid");
            } else {
                playlistGid = MyMusicController.playedSongsPlaylistGid;
            }

            Song song = new Song(songGid, trackJSON.getString("name"), isBuffered, isConverted,
                    artist, playlistGid);



            if (trackJSON.has("image"))
                song.imageURLJSON = trackJSON.getJSONArray("image").toString();
            else if (trackJSON.has("imageURLJSON"))
                song.imageURLJSON = trackJSON.getString("imageURLJSON");

            return song;

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }


    public static List<Song> getSongsFromJSON(String songsJSON) {

        List<Song> songs = new ArrayList<Song>();

        if (songsJSON != null && !songsJSON.equals("")) {

            ArrayList<LinkedTreeMap> oldPlayedSongs = new ArrayList<LinkedTreeMap>();
            oldPlayedSongs = (ArrayList<LinkedTreeMap>) gson.fromJson(songsJSON, oldPlayedSongs.getClass());
            for (int j = 0; j < oldPlayedSongs.size(); j++) {
                LinkedTreeMap songTreeMap = oldPlayedSongs.get(j);
                Song song = getSongFromJSON(gson.toJson(songTreeMap));
                if (song != null)
                    songs.add(song);
            }

        }
        return songs;
    }

    public static String getMD5(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] messageDigest = md.digest(input.getBytes());
            BigInteger number = new BigInteger(1, messageDigest);
            String hashtext = number.toString(16);
            // Now we need to zero pad it if you actually want the full 32 chars.
            while (hashtext.length() < 32) {
                hashtext = "0" + hashtext;
            }
            return hashtext;
        } catch (NoSuchAlgorithmException e) {
            return "";
        }
    }


    public static String getNewID() {
        long timeNow = new Date().getTime();
        MyMusicController.counterGlobalId++;
        String id = getMD5(timeNow + "." + Math.random() + "." + MyMusicController.counterGlobalId);

        if (id.equals(""))
            id = Long.toString(timeNow);

        return "id_" + id;
    }

    public void setPlayedSongs(List<MainListElement> songs) {

        List<MainListElement> oldSongs = playedSongsPlaylist.getList();

        oldSongs.clear();
        oldSongs.addAll(songs);

        //Reload Played songs playlist
        if (UIController.instance.isModeActive(UIController.MYMUSICMODE)) {
            if (MyMusicController.instance.isSubModeActive) {
                if (loadedPlaylist != null && loadedPlaylist.equals(playedSongsPlaylistGid)) {
                    MainActivity.instance.listController.refreshList();

                }
            }
        }

    }


}
