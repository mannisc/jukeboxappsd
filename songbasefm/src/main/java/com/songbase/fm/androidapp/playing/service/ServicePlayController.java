package com.songbase.fm.androidapp.playing.service;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.webkit.URLUtil;
import android.widget.TextView;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.google.android.gms.games.Player;
import com.google.gson.Gson;
import com.songbase.fm.androidapp.MainActivity;
import com.songbase.fm.androidapp.R;
import com.songbase.fm.androidapp.authentication.AuthController;
import com.songbase.fm.androidapp.list.MainListElement;
import com.songbase.fm.androidapp.media.Playlist;
import com.songbase.fm.androidapp.media.PlaylistAction;
import com.songbase.fm.androidapp.media.PlaylistListElement;
import com.songbase.fm.androidapp.media.Song;
import com.songbase.fm.androidapp.media.SongAction;
import com.songbase.fm.androidapp.media.SongListElement;
import com.songbase.fm.androidapp.misc.CustomCallback;
import com.songbase.fm.androidapp.misc.Utils;
import com.songbase.fm.androidapp.mymusic.MyMusicController;
import com.songbase.fm.androidapp.playing.PlayController;
import com.songbase.fm.androidapp.settings.Settings;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Manfred on 24.02.2015.
 */
public class ServicePlayController {


    public MyMediaPlayerService service;

    public static ServicePlayController instance;

    public MediaPlayer mediaPlayer = null;

    public static final String PERSISTENCEDATA_NAME = "SongbaseData";

    public List<Song> playlist = new ArrayList<Song>();

    public Song activeSong = null;
    public String activeURL = null;

    public String activePlaylistGid;

    public boolean isPlaying = false;
    public boolean isLoaded = false;


    public static final Gson gson = new Gson();


    private boolean updateProgress = false;
    private Handler updateProgressHandler = new Handler();

    private ServiceBufferController serviceBufferController;

    private SurfaceView surfaceView;
    private SurfaceHolder surfaceHolder;


    private Runnable updateRunnable = new Runnable() {
        @Override
        public synchronized void run() {

            if (mediaPlayer == null)
                updateProgress = false;
            else {
                Log.d("service.sendPosition", "getpost");
                service.sendPosition(mediaPlayer.getCurrentPosition());
                if (updateProgress)
                    updateProgressHandler.postDelayed(updateRunnable, 500);


            }

        }
    };


    public ServicePlayController(MyMediaPlayerService service, ServiceBufferController serviceBufferController) {
        instance = this;

        this.service = service;

        this.serviceBufferController = serviceBufferController;


    }


    public void initMediaPlayer() {

        if (mediaPlayer != null)
            mediaPlayer.release();

        mediaPlayer = new MediaPlayer();
        mediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {

            @Override
            public boolean onError(MediaPlayer mp, int what, int extras) {
                Log.e("MEDIAPLAYER", "MEDIAPLAYER ERROR");

                if (what == 1 && extras == -1004) {
                    if (!isLoaded) {
                        Log.e("START SONG AGAIN", "!!!!");
                        startSong(activeSong);
                    }
                }


                return true;
            }

        });
        // mediaPlayer.setLooping(true); // this will make it loop forever
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

            @Override
            public void onCompletion(MediaPlayer mp) {
                // TODO Auto-generated method stub
                pause();
                mediaPlayer.seekTo(0);
                instance.service.sendStopped();
                playNext();


            }
        });

        mediaPlayer.setOnBufferingUpdateListener(new MediaPlayer.OnBufferingUpdateListener() {
            @Override
            public void onBufferingUpdate(MediaPlayer mp, int percent) {
                instance.service.sendBufferedPosition(percent);
            }
        });


        mediaPlayer.setOnInfoListener(new MediaPlayer.OnInfoListener() {

            @Override
            public boolean onInfo(MediaPlayer mediaPlayer, int i, int i2) {
                Log.e("MEDIAPLAYER INFO", Integer.toString(i) + " " + Integer.toString(i2) + " " + Boolean.toString(isLoaded));

                if (i == 973) {
                    if (!isLoaded) {
                        Log.e("START SONG AGAIN", "!!!!");

                        startSong(activeSong);
                    }
                }

                return false;
            }

        });

    }


    public void loadVideoUrlOfSong(final Song song) {


        String artistString = song.getArtistName();
        String titleString = song.getName();
        final String searchString = song.getDisplayName();


        String url = Utils.getEncodedUrl(Settings.serverURL + "?play=" + searchString
                + "&force1=" + artistString + "&force2="
                + titleString
                + "&duration=&fromCache=1&auth=" + AuthController.ip_token);

        Log.e("URL", url);
        Log.e("XXX",
                "----------------------------------------------------");
        activeURL = url;
        AjaxCallback<JSONObject> loadSongCallback = new AjaxCallback<JSONObject>() {

            @Override
            public void callback(String url, JSONObject json, AjaxStatus status) {

                Log.e("RESPONSE",
                        "----------------------------------------------------");
                Log.e("activeURL",
                        activeURL);
                Log.e(" url",
                        url);


                if (activeURL.equals(url)) {

                    // status.getCode()

                    if (json != null) {

                        Log.e("JSON", json.toString());

                        try {
                            String streamUrl = json.getString("streamURL");
                            String videoUrl = json.getString("videoURL");

                            Log.e("XXX videoURL", streamUrl);

                            song.setVideoUrl(videoUrl);
                            Log.e("XXX videoURL", videoUrl);

                            Log.e("XXX song " + song.getDisplayName(), Boolean.toString(song.getDisplayName().equals(activeSong.getDisplayName())));
                            Log.e("XXX loaded", Boolean.toString(isLoaded));

                            //Play song
                            if (song.getDisplayName().equals(activeSong.getDisplayName()) && !isLoaded)
                                startPlayer(song, streamUrl);

                        } catch (JSONException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }

                    }
                }
            }

        }.encoding("UTF-16LE").header("Referer", "songbase.fm/app")
                .header("Origin", "songbase.fm/app")
                .header("Host", "songbase.fm:3001");

        AQuery aQuery = new AQuery(this.service);
        aQuery.ajax(url, JSONObject.class, -1, loadSongCallback);

    }

    public void startPlayer(final Song song, final String songPath) {
        Log.e("LLL", "START PLAYER");

        service.createNotifcation();
        initMediaPlayer();
        Thread player = new Thread(new Runnable() {

            @Override
            public void run() {
                final MediaPlayer actMediaPlayer = mediaPlayer;
                // TODO Auto-generated method stub
                Boolean prepared;

                try {
                    File f = new File(songPath);
                    Log.e("XXXXX MP3 FILE ...:", songPath + "  " + songPath.contains("com.songbase.fm"));
                    Log.e("EXISTS?", Boolean.toString((f.exists() && !f.isDirectory())));
                    actMediaPlayer.setDataSource(songPath);
                    if (!songPath.contains("com.songbase.fm"))
                        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

                    actMediaPlayer.prepare();
                    prepared = true;

                } catch (IllegalArgumentException e) {
                    prepared = false;
                    e.printStackTrace();
                } catch (SecurityException e) {
                    prepared = false;
                    e.printStackTrace();
                } catch (IllegalStateException e) {
                    prepared = false;
                    e.printStackTrace();
                } catch (IOException e) {
                    prepared = false;
                    e.printStackTrace();
                }
                Log.e("PREPARED?", Boolean.toString(prepared) + "   " + song.getDisplayName() + " = " + activeSong.getDisplayName() + " :  " + song.getDisplayName().equals(activeSong.getDisplayName()));
                if (prepared) {


                    if (song.getDisplayName().equals(activeSong.getDisplayName())) {
                        isLoaded = true;

                        //Send Duration
                        int duration = -1;
                        while (duration <= 0 && MyMediaPlayerService.isRunning && song.getDisplayName().equals(activeSong.getDisplayName())) {
                            duration = ServicePlayController.instance.getDuration();
                            Log.e("DURATION?", Integer.toString(duration));
                            if (duration <= 0) {
                                try {
                                    Thread.sleep(100);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }

                        }
                        if (song.getDisplayName().equals(activeSong.getDisplayName())) {
                            service.sendDuration(duration);
                            service.sendLoaded();
                            if (isPlaying)
                                play();
                        }

                    } else
                        actMediaPlayer.stop();

                }else{
                    service.sendDuration(-1);
                    service.sendLoaded();
                }





            }
        });
        player.start();

        //   new Player().execute(song, songPath);
    }

    public void startSong(Song song) {


        this.activeSong = song;
        this.isLoaded = false;
        this.isPlaying = true;


        String newSongPath = null;

        if (!activeSong.isConverted && !activeSong.isBuffered)
            loadVideoUrlOfSong(activeSong);
        else {
            if (activeSong.isConverted)
                newSongPath = serviceBufferController.getSongPath(this.activeSong);
            else if (activeSong.isBuffered)
                newSongPath = serviceBufferController.getSongPath(this.activeSong);
            //New Song should be started
            if (newSongPath != null && song.getDisplayName().equals(activeSong.getDisplayName())) {
                Log.e("XXXXX MP3 FILE:", newSongPath);
                this.startPlayer(song, newSongPath);
            }

        }


        saveActiveSong();

        service.sendInfo();

    }

    public synchronized void play() {
        Log.e("!!!!PLAY Music ", "++++++");

        if (isLoaded && !mediaPlayer.isPlaying()) {
            updateProgress = true;
            updateProgressHandler.removeCallbacksAndMessages(null);
            updateProgressHandler.post(updateRunnable);
            mediaPlayer.start();
        }
        isPlaying = true;

    }

    public synchronized void pause() {
        Log.e("!!!!PAUSE Music", "++++++");

        isPlaying = false;
        updateProgress = false;
        if (isLoaded && mediaPlayer.isPlaying())
            mediaPlayer.pause();


    }

    public synchronized void stop() {
        Log.e("!!!!STOP Music", "++++++");

        if (isPlaying) {
            isPlaying = false;
            if (mediaPlayer != null) {
                mediaPlayer.release();
                mediaPlayer = null;
            }
            service.stopForeground(true);
        }
    }

    public synchronized void reset() {
        updateProgressHandler.removeCallbacksAndMessages(null);
        if (mediaPlayer != null) {
            mediaPlayer.setDisplay(null);
            mediaPlayer.reset();
        }
        service.sendReset(0);
    }


    public synchronized void playPrev() {
        if (activeSong != null) {
            Song nextSong;

            reset();

            if (playlist.size() > 0) {

                int index = getSongIndex(playlist, activeSong);

                Log.e("!!!!PLAY Next Song ", Integer.toString(index) + " < " + Integer.toString(playlist.size() - 1));

                if (index == -1) {
                    Log.e("!!!!P!!!!!!!!!!!! ", "SONG NOT FOUND IN PLAYLIST");
                    nextSong = playlist.get(0);
                } else if (index > 0)
                    nextSong = playlist.get(index - 1);
                else
                    nextSong = playlist.get(playlist.size() - 1);

            } else
                nextSong = activeSong;


            startSong(nextSong);


        }
    }


    public synchronized void playNext() {
        if (activeSong != null) {
            Song nextSong;

            reset();


            if (playlist.size() > 0) {
                int index = getSongIndex(playlist, activeSong);

                Log.e("!!!!PLAY Next Song ", Integer.toString(index) + " < " + Integer.toString(playlist.size() - 1));


                if (index == -1) {
                    Log.e("!!!!P!!!!!!!!!!!! ", "SONG NOT FOUND IN PLAYLIST");
                    nextSong = playlist.get(0);
                } else if (index < playlist.size() - 1)
                    nextSong = playlist.get(index + 1);
                else
                    nextSong = playlist.get(0);

            } else
                nextSong = activeSong;


            startSong(nextSong);


        }
    }


    public int getSongIndex(List<Song> playlist, Song song) {
        int returnValue = -1;
        for (int i = 0; i < playlist.size(); i++) {
            if (song.getDisplayName().equals(playlist.get(i).getDisplayName()) && song.gid.equals(playlist.get(i).gid)) {
                returnValue = i;
                break;
            }
        }
        return returnValue;
    }


    public int getDuration() {
        if (this.isLoaded)
            return mediaPlayer.getDuration();
        else
            return -1;

    }


    public void setPosition(int position) {
        if (position >= 0 && position < this.getDuration())
            mediaPlayer.seekTo(position);
    }

    public void setPositionPercent(int positionPercent) {
        float position = -1;
        if (positionPercent >= 0)
            position = positionPercent / 100.0f * this.getDuration();
        setPosition((int) position);

    }

    private class LoadingCallback implements CustomCallback {
        @Override
        public void callbackString(String returnString) {
        }
    }


    public void saveActiveSong() {


        SharedPreferences data = service.getApplicationContext().getSharedPreferences(PERSISTENCEDATA_NAME, 0);
        SharedPreferences.Editor editor = data.edit();
        editor.putString("activeSong", gson.toJson(activeSong));
        editor.putString("activePlaylistGid", gson.toJson(activePlaylistGid));
        editor.commit();

    }


    public void getPlaylistSongs(String gid, boolean forceUpdate) {

        Log.e("getPlaylistSongs",gid);
        Log.e("getPlaylistSongs? 0",Boolean.toString(gid.equals("0")));




        if (!gid.equals(activePlaylistGid) || forceUpdate) {

            List<Song> songs = new ArrayList<Song>();

            SharedPreferences data = service.getApplicationContext().getSharedPreferences(PERSISTENCEDATA_NAME, 0);
            String playlistsJSON = data.getString("playlists", "{}");

            try {

                JSONObject json = new JSONObject(playlistsJSON);
                JSONArray playlistMatches = json.getJSONArray("items");
                Log.e("getPlaylistSongs p",Integer.toString(playlistMatches.length()));

                for (int i = 0; i < playlistMatches.length(); i++) {
                    JSONObject playlistJSON = playlistMatches.getJSONObject(i);

                    String actGid = playlistJSON.getString("gid");
                    Log.e("getPlaylistSongs..","?");

                    if (actGid.equals(gid) || gid.equals(MyMusicController.allSongsPlaylistGid)) {

                        Log.e("getPlaylistSongs..","!");

                        JSONArray playlistSongs = playlistJSON.getJSONArray("data");

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

                            boolean isBuffered = trackJSON.getBoolean("isBuffered");
                            boolean isConverted = trackJSON.getBoolean("isConverted");

                            Song track = new Song(trackJSON.getString("gid"), trackJSON.getString("name"), isBuffered, isConverted,
                                    artist, trackJSON.getString("playlistgid"));

                            songs.add(track);

                        }


                    }

                }

                playlist.clear();
                playlist = songs;
                activePlaylistGid = gid;

            } catch (JSONException e) {
                e.printStackTrace();
            }

            Log.e("SONGS IN PLAYLIST", Integer.toString(playlist.size()));
        }
    }

}
