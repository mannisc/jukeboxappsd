package com.songbase.fm.androidapp.buffering.online;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.media.MediaPlayer;
import android.os.Environment;
import android.util.Log;

import com.songbase.fm.androidapp.MainActivity;
import com.songbase.fm.androidapp.buffering.BufferController;
import com.songbase.fm.androidapp.buffering.Vid2mp3Converter;
import com.songbase.fm.androidapp.media.Song;
import com.songbase.fm.androidapp.misc.CustomCallback;
import com.songbase.fm.androidapp.misc.HttpController;
import com.songbase.fm.androidapp.misc.Utils;

public class Convert2mp3netConverter implements Vid2mp3Converter {

    private final String HOST = "convert2mp3.net";


    public BufferController bufferController;

    public Convert2mp3netConverter(BufferController bufferController) {
        this.bufferController = bufferController;
    }

    @Override
    public boolean bufferSong(Song song, String streamUrl, String videoUrl, CustomCallback callback) {

        try {


            Log.e("DONLOAD2mp3", "STAAAART Convert2mp3net ");
            Log.e("DONLOAD2mp3", videoUrl);




            String urlParameters = "url=" + URLEncoder.encode(videoUrl)
                    + "&format=mp3&quality=1&85tvb5=43450768";
            String htmlConversionStart = HttpController.instance.sendPost(
                    "http://" + HOST + "/index.php?p=convert", urlParameters,
                    HOST);

            Pattern pattern = Pattern.compile("convert\\((.*?)\\)");
            Matcher matcher = pattern.matcher(htmlConversionStart);
            if (matcher.find()) {

                // Get Video id and key
                String parameters = matcher.group(1).replace("\"", "")
                        .replace(" ", "");

                String[] parameter = parameters.split(",");

                String id = parameter[0];
                String key = parameter[1];
                // cs:parameter[2];
                // format:parameter[3]

                pattern = Pattern.compile("\"convertFrame\" src=\"(.*?)\"");
                matcher = pattern.matcher(htmlConversionStart);
                if (matcher.find()) {
                    Log.e("DONLOAD2mp3", "STAAAART Convert2mp3net succ");

                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException ex) {
                        Thread.currentThread().interrupt();
                    }
                    // Trigger Conversion
                    String triggerConversionURL = matcher.group(1);

                    pattern = Pattern.compile("http://(.*?)/");
                    matcher = pattern.matcher(triggerConversionURL);
                    if (matcher.find()) {

                        Log.e("DONLOAD2mp3", "STAAAART Convert2mp3net succ2");

                        String host = matcher.group(1);

						//Trigger the coversion
                        HttpController.instance
                                .sendGet(triggerConversionURL, host, "http://"
										+ HOST + "/index.php?p=convert");


                        String completeURL = "http://" + HOST
                                + "/index.php?p=complete&id=" + id + "&key="
                                + key;



                        String htmlComplete = HttpController.instance.sendGet(
                                completeURL, HOST, "http://" + HOST
                                        + "/index.php?p=convert");





                        pattern = Pattern
                                .compile("btn-success btn-large\" href=\"(.*?)\"");
                        matcher = pattern.matcher(htmlComplete);

                        if (matcher.find()) {

                            String downloadURL = matcher.group(1);
                            Log.e("DONLOAD2mp3", downloadURL);
                            final String songFilePath = bufferController.getSongPath(song);
                            boolean success = HttpController.instance.sendGetBinaryToFile(downloadURL, songFilePath);

                            if (!success) {
                                if (callback != null)
                                    callback.callbackString(null);

                            } else {
                                Log.e("DONLOAD2mp3", "Successfully converted");

                                song.isBuffered = true;
                                song.isConverted = true;

                                if (callback != null)
                                    callback.callbackString(downloadURL);

                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            if (callback != null)
                callback.callbackString(null);


            return false;

        }
        return true;
    }
}
