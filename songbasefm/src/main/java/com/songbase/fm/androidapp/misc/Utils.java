package com.songbase.fm.androidapp.misc;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.Rect;
import android.net.ConnectivityManager;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.songbase.fm.androidapp.MainActivity;

public class Utils {
    /**
     * Divides a string into chunks of a given character size.
     *
     * @param text      String text to be sliced
     * @param sliceSize int Number of characters
     * @return ArrayList<String>   Chunks of strings
     */
    public static ArrayList<String> splitString(String text, int sliceSize) {
        ArrayList<String> textList = new ArrayList<String>();
        String aux;
        int left = -1, right = 0;
        int charsLeft = text.length();
        while (charsLeft != 0) {
            left = right;
            if (charsLeft >= sliceSize) {
                right += sliceSize;
                charsLeft -= sliceSize;
            } else {
                right = text.length();
                aux = text.substring(left, right);
                charsLeft = 0;
            }
            aux = text.substring(left, right);
            textList.add(aux);
        }
        return textList;
    }

    /**
     * Divides a string into chunks.
     *
     * @param text String text to be sliced
     * @return ArrayList<String>
     */
    public static ArrayList<String> splitString(String text) {
        return splitString(text, 80);
    }

    /**
     * Divides the string into chunks for displaying them
     * into the Eclipse's LogCat.
     *
     * @param text The text to be split and shown in LogCat
     * @param tag  The tag in which it will be shown.
     */
    public static void splitAndLog(String tag, String text) {
        ArrayList<String> messageList = Utils.splitString(text);
        for (String message : messageList) {
            Log.d(tag, message);
        }
    }

    /**
     * Uses short and simple syntax and divides the string into chunks for displaying them
     * into the Eclipse's LogCat.
     *
     * @param text The text to be split and shown in LogCat
     */
    public static void log(String text) {
        ArrayList<String> messageList = Utils.splitString(text);
        for (String message : messageList) {
            Log.d("log", message);
        }
    }

    /**
     * Get the data download settings
     * @return 0 high limitation
     * 1 middle limitation
     * 2 no limitation
     */

    public static int getDownloadDataLimitation() {

        return  checkNetworkStatus();

    }

    /**
     * RGet the network connection status
     * @return Coonection type
     * 0 No Network
     * 1 3g
     * 2 wifi
     */

    public static int checkNetworkStatus() {

        return 2;

        /*

        final ConnectivityManager connMgr = (ConnectivityManager)
                MainActivity.instance.getSystemService(Context.CONNECTIVITY_SERVICE);

        final android.net.NetworkInfo wifi =
                connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        final android.net.NetworkInfo mobile =
                connMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

        if (wifi.isAvailable()&&wifi.isConnectedOrConnecting()) {

            Toast.makeText(MainActivity.instance, "Wifi", Toast.LENGTH_LONG).show();
            return 2;
        } else if (mobile.isAvailable()&&mobile.isConnectedOrConnecting()) {


            Toast.makeText(MainActivity.instance, "Mobile 3G ", Toast.LENGTH_LONG).show();
            return 1;
        } else {

            Toast.makeText(MainActivity.instance, "No Network ", Toast.LENGTH_LONG).show();
            return 0;

        }
      */
    }


    public static void debug(String string) {
        Toast.makeText(MainActivity.instance, string, Toast.LENGTH_LONG).show();
        Log.e("###UTILS DEBUG :", string);
    }

    public static int getTextViewTextWidth(TextView view, String text) {
        Rect bounds = new Rect();
        Paint textPaint = view.getPaint();
        textPaint.getTextBounds(text, 0, text.length(), bounds);
        //   int height = bounds.height();
        int width = bounds.width();
        return width;
    }

    public static String getEncodedUrl(String urlStr) {
        URL url;
        try {
            url = new URL(urlStr);
            URI uri = new URI(url.getProtocol(), url.getUserInfo(), url.getHost(), url.getPort(), url.getPath(), url.getQuery(), url.getRef());
            return uri.toASCIIString();
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return "";

        } catch (URISyntaxException e) {
            e.printStackTrace();
            return "";
        }
    }


}


//MOCK SERVER RESONSE
/*
                String jsonSTRING = "{\"streamURL\":\"http://h2406563.stratoserver.net/mustang.mp4\",\"videoURL\":\"http://www.dailymotion.com/video/x25ud6r_calvin-harris-ft-john-newman-blame-official-video-hd-720p_music\"}";
                if (Math.random() * 10.0f <= 5.0f)
                    jsonSTRING = "{\"streamURL\":\"http://h2406563.stratoserver.net/LostandFound.mp3\",\"videoURL\":\"http://www.dailymotion.com/video/x25ud6r_calvin-harris-ft-john-newman-blame-official-video-hd-720p_music\"}";
                Log.e("XXX", jsonSTRING);
                try {
                    json = new JSONObject(jsonSTRING);
                } catch (JSONException e) {
                    Toast.makeText(aQuery.getContext(),
                            "Error:" + status.getCode(), Toast.LENGTH_LONG)
                            .show();
                    e.printStackTrace();
                }
                */