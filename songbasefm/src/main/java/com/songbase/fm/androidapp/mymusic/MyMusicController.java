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

public class MyMusicController {


    public List<MainListElement> list = new ArrayList<MainListElement>();

    public static MyMusicController instance;

    public AQuery aQuery;
    public static Gson gson = new Gson();


    public boolean isSubModeActive = false;
    public String activePlaylistGid;

    public static long counterGlobalId = 0;

    public static String allSongsPlaylistGid = "id_0";


    public MyMusicController(List<MainListElement> list) {
        MyMusicController.instance = this;
        this.list = list;
        aQuery = MainActivity.instance.aQuery;


    }


    public void setPlaylistList(List<PlaylistListElement> list) {

        this.list.clear();
        this.list.addAll(list);

        if (PlayController.instance.activeSong != null)
            PlayController.instance.setActivePlaylistByGid(PlayController.instance.activeSong.getPlaylistGid());


        Log.e("..c", Integer.toString(list.size()));


    }


    public String getPlaylistsAsJSON() {


        String playlistsJSON = "{\"items\": [";

        Log.e("..", Integer.toString(list.size()));

        for (MainListElement playlistElement : list) {

            Playlist playlist = ((PlaylistListElement) playlistElement).getPlaylist();

            if (!playlist.getGid().equals(MyMusicController.allSongsPlaylistGid)) {

                String playlistJSON = "{ \"name\": \"" + playlist.getName() + "\",\"gid\": \"" + playlist.getGid() + "\",\"data\": [";

                String songsJSON = "";
                List<MainListElement> playlistSongs = playlist.getList();

                for (MainListElement songElement : playlistSongs) {
                    Song song = ((SongListElement) songElement).getSong();
                    songsJSON = songsJSON + gson.toJson(song);
                    if (playlistSongs.indexOf(songElement) < playlistSongs.size() - 1)
                        songsJSON = songsJSON + ",";
                }

                //Insert songs into playlist
                playlistJSON = playlistJSON + songsJSON;
                playlistJSON = playlistJSON + "]}";

                //insert Playlist into Playlist list
                playlistsJSON = playlistsJSON + playlistJSON;
                if (list.indexOf(playlistElement) < list.size() - 1)
                    playlistsJSON = playlistsJSON + ",";


            }

        }

        playlistsJSON = playlistsJSON + "]}";
        return playlistsJSON;

    }


    public static List<PlaylistListElement> parsePlaylistJSON(JSONObject json) {
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

                    for (int j = 0; j < playlistSongs.length(); j++) {
                        JSONObject trackJSON = playlistSongs.getJSONObject(j);

                        String artist;
                        try {
                            JSONObject artistJSON = trackJSON
                                    .getJSONObject("artist");
                            artist = artistJSON.getString("name");
                        } catch (JSONException e) {
                            artist = trackJSON.getString("artist");
                        }

                        boolean isBuffered;
                        try {
                            isBuffered = trackJSON.getBoolean("isBuffered");
                        } catch (JSONException e) {
                            isBuffered = false;
                        }
                        boolean isConverted;
                        try {
                            isConverted = trackJSON.getBoolean("isConverted");
                        } catch (JSONException e) {
                            isConverted = false;//TODO
                        }


                        Song track = new Song(trackJSON.getString("gid"), trackJSON.getString("name"), isBuffered, isConverted,
                                artist, trackJSON.getString("playlistgid"));

                        songs.add(new SongListElement(track, new SongAction(
                                track)));




                        //Add to all songs Playlist
                        Song trackAll = new Song(trackJSON.getString("gid"), trackJSON.getString("name"), isBuffered, isConverted,
                                artist, MyMusicController.allSongsPlaylistGid);


                        trackAll.album = playlistJSON.getString("name");

                        allSongs.add(new SongListElement(trackAll, new SongAction(
                                trackAll)));


                        try {
                            String image =  ((JSONObject)trackJSON.getJSONArray("image").get(2)).getString("#text");
                            List<Song> songsIcon = new ArrayList<Song>();
                            songsIcon.add(trackAll);
                            songsIcon.add(track);

                            Song.loadIcon(image,songsIcon);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


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
        list.add(0, new PlaylistListElement(playlist,
                new PlaylistAction(playlist)));

        return list;
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

}
