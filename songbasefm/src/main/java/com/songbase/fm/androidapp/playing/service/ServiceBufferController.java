package com.songbase.fm.androidapp.playing.service;

import android.app.Service;

import com.songbase.fm.androidapp.MainActivity;
import com.songbase.fm.androidapp.buffering.BufferController;
import com.songbase.fm.androidapp.buffering.online.Convert2mp3netConverter;
import com.songbase.fm.androidapp.media.Song;

import java.io.File;

/**
 * Created by Manfred on 24.02.2015.
 */
public class ServiceBufferController {

    public File bufferDirectory;

    public MyMediaPlayerService service;

    private static ServiceBufferController instance;

    public ServiceBufferController(MyMediaPlayerService service) {

        instance = this;

        this.service = service;
        bufferDirectory = this.service.getDir("buffer", MainActivity.instance.MODE_PRIVATE);
    }


    public String getSongPath(Song song) {
        File privateDir = this.bufferDirectory;
        return privateDir.getAbsolutePath() + File.separator + Integer.toString(song.getDisplayName().hashCode()) + ".mp3";
    }

    public String getSongVideoPath(Song song) {
        final File privateDir = this.bufferDirectory;
        return privateDir.getAbsolutePath() + File.separator + Integer.toString(song.getDisplayName().hashCode()) + ".tmp";
    }



}
