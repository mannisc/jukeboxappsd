package com.songbase.fm.androidapp.buffering;

import android.content.Intent;
import android.graphics.Path;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.os.Messenger;
import android.util.Log;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.songbase.fm.androidapp.MainActivity;
import com.songbase.fm.androidapp.authentication.AuthController;
import com.songbase.fm.androidapp.buffering.offline.Offline2mp3Converter;
import com.songbase.fm.androidapp.buffering.online.Convert2mp3netConverter;
import com.songbase.fm.androidapp.media.Song;
import com.songbase.fm.androidapp.misc.CustomCallback;
import com.songbase.fm.androidapp.misc.Utils;
import com.songbase.fm.androidapp.playing.service.MyMediaPlayerService;
import com.songbase.fm.androidapp.playing.service.ServiceMessageHandler;
import com.songbase.fm.androidapp.settings.Settings;

import org.apache.commons.io.FileUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.Arrays;

public class BufferController {

    private Vid2mp3Converter vid2mp3Converter;

    public File bufferDirectory;

    public static AQuery aQuery;

    private static BufferController instance;

    public BufferController() {

        BufferController.instance = this;


        //this.vid2mp3Converter = new Offline2mp3Converter(this);

        this.vid2mp3Converter = new Convert2mp3netConverter(this);

        /**
         * Debugging //TODO REMOVE
         */


        // String[] files = MainActivity.instance.fileList();
        //  Log.e("XXXX FILES:", Arrays.toString(files));

        bufferDirectory = MainActivity.instance.getDir("buffer", MainActivity.instance.MODE_PRIVATE); //MainActivity.instance.getFilesDir();

        //  DISPLAY buffered SONGS
        File[] entries = bufferDirectory.listFiles();
        for (File inFile : entries) {
            Log.e("XXXX FILE:", inFile.getName() + "  " + Float.toString(((float) inFile.length() / 1024.0f / 1024.0f)));
            if (inFile.isDirectory()) {
                Log.e("XXXX FILE:", "Directory");
                // is directory
            }
        }
        //Clear buffered Directory
        /*
        String[] children = bufferDirectory.list();
        for (int i = 0; i < children.length; i++) {
            new File(bufferDirectory, children[i]).delete();
        }
        */

        //String mp3File = this.bufferDirectory.getAbsolutePath() + File.separator + "downloaded2.mp3";
        //MainActivity.instance.playController.startSongInService(mp3File);


    }


    public void bufferSong(final Song song) {

        this.loadVideoUrlOfSong(song, new LoadingCallback() {

            @Override
            public void callback(String streamUrl, String videoUrl) {
                if (streamUrl != null || videoUrl != null)
                    bufferSong(song, streamUrl, videoUrl, null);
            }
        });
    }

    public void bufferSong(Song song, String streamUrl, String videoUrl, CustomCallback callback) {

        BufferTask bufferTask = new BufferTask();

        bufferTask.execute(song, streamUrl, videoUrl, callback);

    }

    public String getSongPath(Song song) {
        File privateDir = this.bufferDirectory;
        return privateDir.getAbsolutePath() + File.separator + Integer.toString(song.getDisplayName().hashCode()) + ".mp3";
    }

    public String getSongVideoPath(Song song) {
        final File privateDir = this.bufferDirectory;
        return privateDir.getAbsolutePath() + File.separator + Integer.toString(song.getDisplayName().hashCode()) + ".tmp";
    }

    public String createSongVideoFile(Song song) {

        String tempFileSuffix = ".tmp";
        final File privateDir = this.bufferDirectory;
        String songVideoFileName = Integer.toString(song.getDisplayName().hashCode());

        try {
            File.createTempFile(songVideoFileName, tempFileSuffix, privateDir);
            return privateDir + File.separator + songVideoFileName + tempFileSuffix;

        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }

    }


    private class BufferTask extends AsyncTask<Object, Void, String> {

        @Override
        protected String doInBackground(Object... params) {

            Log.e("Buffering","Start "+ params[1]+" "+ params[2]);

            BufferController.instance.vid2mp3Converter.bufferSong(
                    (Song) params[0], (String) params[1], (String) params[2], (CustomCallback) params[3]);

            return "";

        }

        @Override
        protected void onPostExecute(String result) {

            //Maybe another sub buffer thread still on
            Log.e("", "Done buffering");
        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }
    }


    public void loadVideoUrlOfSong(final Song song, final LoadingCallback callback) {

        aQuery = new AQuery(MainActivity.instance);

        String artistString = song.getArtistName();
        String titleString = song.getName();
        final String searchString = song.getDisplayName();

        String url = Utils.getEncodedUrl(Settings.serverURL + "?play=" + searchString
                + "&force1=" + artistString + "&force2="
                + titleString
                + "&duration=&fromCache=1&auth=" + AuthController.ip_token);

        Log.e("XXX", url);

        AjaxCallback<JSONObject> loadSongCallback = new AjaxCallback<JSONObject>() {

            @Override
            public void callback(String url, JSONObject json, AjaxStatus status) {

                // status.getCode()
                Log.e("loadSongCallback", url+" "+status.toString());

                /*
                String jsonSTRING = "{\"streamURL\":\"http://h2406563.stratoserver.net/mustang.mp4\",\"videoURL\":\"http://www.dailymotion.com/video/x7c8og_rammstein-moskau_music\"}";

                Log.e("XXX", jsonSTRING);

                try {
                    json = new JSONObject(jsonSTRING);
                } catch (JSONException e) {
                    Toast.makeText(aQuery.getContext(),
                            "Error:" + status.getCode(), Toast.LENGTH_LONG)
                            .show();
                    e.printStackTrace();
                }*/

                if (json != null) {
                    Log.e("loadSongCallback response", json.toString());

                    try {
                        String streamUrl = json.getString("streamURL");
                        String videoUrl = json.getString("videoURL");

                        Log.e("loadSongCallback streamUrl", streamUrl);
                        Log.e("loadSongCallback videoURL", videoUrl);

                        song.setVideoUrl(videoUrl);

                        if (callback != null)
                            callback.callback(streamUrl, videoUrl);

                    } catch (JSONException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();

                        if (callback != null)
                            callback.callback(null, null);

                    }

                }
            }

        }.encoding("UTF-16LE").header("Referer", "songbase.fm/app")
                .header("Origin", "songbase.fm/app")
                .header("Host", "songbase.fm:3001");

        aQuery.ajax(url, JSONObject.class, loadSongCallback);

    }


    private class LoadingCallback {
        public void callback(String streamUrl, String videoUrl) {
        }
    }

}
