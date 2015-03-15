package com.songbase.fm.androidapp.playing.service;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.google.gson.Gson;
import com.songbase.fm.androidapp.MainActivity;
import com.songbase.fm.androidapp.media.Song;
import com.songbase.fm.androidapp.playing.PlayController;

public class ServiceMessageHandler extends Handler {

    public static final String MESSAGETYPE = "TYPE";
    public static final String MESSAGETYPE_DURATION = "DURATION";
    public static final String MESSAGETYPE_POSITION = "POSITION";
    public static final String MESSAGETYPE_INFO = "INFO";
    public static final String MESSAGETYPE_STOPPED = "STOPPED";
    public static final String MESSAGETYPE_BUFFERED = "BUFFERED";
    public static final String MESSAGETYPE_RESET = "RESET";
    public static final String MESSAGETYPE_LOADED = "LOADED";

    public static final String MESSAGETYPE_PLAYEDSONGSUPDATE = "PLAYEDSONGSUPDATE";


    public static final Gson gson = new Gson();

    @Override
    public void handleMessage(Message message) {


        if (MainActivity.instance != null && MainActivity.instance.loaded) {

            Bundle data = message.getData();


            if (data.getString(MESSAGETYPE).equals(MESSAGETYPE_STOPPED)) {


                PlayController.instance.isPlaying = false;
                PlayController.instance.onCurrentPositionChanged(0);
                PlayController.instance.updateShowedPosition();

                PlayController.instance.onPause();


            } else if (data.getString(MESSAGETYPE).equals(MESSAGETYPE_LOADED)) {

                PlayController.instance.onLoaded();


            } else if (data.getString(MESSAGETYPE).equals(MESSAGETYPE_DURATION)) {

                PlayController.instance.onDurationChanged(data
                        .getInt("DURATIONVALUE"));


            } else if (data.getString(MESSAGETYPE).equals(MESSAGETYPE_BUFFERED)) {

                PlayController.instance.onCurrentBufferingPositionChanged(data
                        .getInt("BUFFEREDVALUE"));

            } else if (data.getString(MESSAGETYPE).equals(MESSAGETYPE_POSITION)) {

                PlayController.instance.onCurrentPositionChanged(data
                        .getInt("POSITIONVALUE"));

            } else if (data.getString(MESSAGETYPE).equals(MESSAGETYPE_INFO)) {

                try {

                    Song playedSong = gson.fromJson(data.getString("SONG"), Song.class);
                    if (PlayController.instance.activeSong == null || !playedSong.gid.equals(PlayController.instance.activeSong.gid)) {
                        Song localSong = PlayController.instance.getSongFromSongGid(playedSong.gid);
                        if (localSong != null) {
                            PlayController.instance.setPlayingSongInfo(localSong);
                        }
                    }

                    boolean newIsPlaying = data.getBoolean("ISPLAYING", false);

                    if (newIsPlaying != PlayController.instance.isPlaying) {
                        PlayController.instance.isPlaying = newIsPlaying;
                        if (PlayController.instance.isPlaying) {
                            PlayController.instance.onPlay();
                        } else {
                            PlayController.instance.onPause();
                        }
                    }

                } catch (com.google.gson.JsonSyntaxException e) {
                }

            } else if (data.getString(MESSAGETYPE).equals(MESSAGETYPE_RESET)) {

                PlayController.instance.onReset();

            } else if (data.getString(MESSAGETYPE).equals(MESSAGETYPE_PLAYEDSONGSUPDATE)) {

                String songsJSON = data.getString("SONGS");
                PlayController.instance.onPlayedSongsUpdate(songsJSON);

            }
        }
    }
}