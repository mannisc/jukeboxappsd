package com.songbase.fm.androidapp.buffering.offline;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.songbase.fm.androidapp.MainActivity;
import com.songbase.fm.androidapp.buffering.BufferController;
import com.songbase.fm.androidapp.buffering.Vid2mp3Converter;
import com.songbase.fm.androidapp.buffering.offline.ffmpeg.Clip;
import com.songbase.fm.androidapp.buffering.offline.ffmpeg.FfmpegController;
import com.songbase.fm.androidapp.buffering.offline.ffmpeg.ShellUtils.ShellCallback;
import com.songbase.fm.androidapp.media.Song;
import com.songbase.fm.androidapp.misc.CustomCallback;
import com.songbase.fm.androidapp.misc.HttpController;

public class Offline2mp3Converter implements Vid2mp3Converter {

    private final String tempFileName = "songbasetmpfile";

    public BufferController bufferController;

    public Offline2mp3Converter(BufferController bufferController) {
        this.bufferController = bufferController;
    }

    @Override
    public boolean bufferSong(final Song song, String streamUrl ,String videoUrl,final CustomCallback callback) {
        // TODO Auto-generated method stub

        Log.e("", "DOWNLOADING SONG");

        final String songVideoFilePath = bufferController.createSongVideoFile(song);
        boolean success = HttpController.instance.sendGetBinaryToFile(streamUrl, songVideoFilePath);

        if (!success) {
            if (callback != null) {
                callback.callbackString(null);
            }
        } else {

            song.isBuffered = true;

            // referer)

            Log.e("", "DOWNLOADING DONE");

            //TODO: Check filetype for ffmpeg?

            Log.e("", "Buffering mp3 SONG");
            try {
                File cacheDir = MainActivity.instance.getCacheDir();

                FfmpegController ffmpegController = new FfmpegController(
                        MainActivity.instance, File.createTempFile(tempFileName + "vidlib.tmp", null, cacheDir));

                Clip clip = new Clip(songVideoFilePath);


                final String songFilePath = bufferController.getSongPath(song);


                Log.e("CREATE MP3 FILE: ", songFilePath );

                File songFile = new File(songFilePath);


                ffmpegController.convertToMP3(clip, songFile, new ShellCallback() {

                    @Override
                    public void shellOut(String msg) {
                        System.out.print(msg);
                    }

                    @Override
                    public void processComplete(int exitValue) {

                        // TODO Auto-generated method stub
                        Log.e("!!!!", "COMPLETE");

                        song.isConverted = true;

                        //Delete Video File
                        new File(songVideoFilePath).delete();



                        if (callback != null) {
                            callback.callbackString(songFilePath);
                        }

                    }
                });

            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }
        return false;

    }

}
