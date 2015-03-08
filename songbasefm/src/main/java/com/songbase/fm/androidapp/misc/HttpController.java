package com.songbase.fm.androidapp.misc;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.commons.io.IOUtils;

import android.util.Log;

import com.songbase.fm.androidapp.MainActivity;

public class HttpController {

    public static HttpController instance = new HttpController();
    ;

    private final String USER_AGENT = "Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/36.0.1985.125 Safari/537.36";

    // HTTP GET request
    public String sendGet(String url, String host, String referer) {
        HttpURLConnection connection = null;
        String html = "";
        try {

            URL obj = new URL(url);
            connection = (HttpURLConnection) obj.openConnection();
            connection.setConnectTimeout(30000); // set timeout to 30 seconds
            // optional default is GET
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Accept", "*/*");
            connection.setRequestProperty("Cache-Control", "no-cache");
            connection.setRequestProperty("Connection", "keep-alive");
            connection.setRequestProperty("Host", host);
            connection.setRequestProperty("Pragma", "no-cache");
            connection.setRequestProperty("Referer", referer);
            connection.setRequestProperty("User-Agent", USER_AGENT);

			/*
             * int responseCode = connection.getResponseCode();
			 * System.out.println("\nSending 'GET' request to URL : " + url);
			 * System.out.println("Response Code : " + responseCode);
			 * System.out.println("Response Len : " +
			 * connection.getContentLength());
			 */

            html = IOUtils.toString(connection.getInputStream(), "UTF-8");

        } catch (Exception ex) {
            Log.e("", "ERROR IN GET REQUEST");

        } finally {

            if (connection != null) {
                connection.disconnect();
            }
        }

        return html;
    }




    // HTTP GET request
    public boolean sendGetBinaryToFile(String url,String filePath) {
        HttpURLConnection connection = null;

        boolean error = false;


        try {

            FileOutputStream fileOutput = new FileOutputStream(filePath);

            URL obj = new URL(url);

           // ByteArrayOutputStream bais = new ByteArrayOutputStream();
            InputStream is = null;
            try {
                try {
                    is = obj.openStream();
                    byte[] byteChunk = new byte[4096]; // Or whatever size you
                    // want to read in at a
                    // time.
                    int n;
                    while ((n = is.read(byteChunk)) > 0) {
                        fileOutput.write(byteChunk, 0, n);
                    }
                } catch (OutOfMemoryError e) {
                    System.err.printf("Out of Memory", obj.toExternalForm(),
                            e.getMessage());
                    e.printStackTrace(); // TODO outofmemory handling
                    // Perform any other exception handling that's appropriate.
                    error = true;

                }
            } catch (IOException e) {
                System.err.printf("Failed while reading bytes from %s: %s",
                        obj.toExternalForm(), e.getMessage());
                e.printStackTrace();
                error = true;
                // Perform any other exception handling that's appropriate.
            } finally {
                if (is != null) {
                    is.close();
                }
            }


           // data = bais.toByteArray();

            fileOutput.close();


        } catch (Exception ex) {
            Log.e("", "ERROR IN GET REQUEST");
            error = true;


        } finally {

            if (connection != null) {
                connection.disconnect();
            }
        }

        return !error;
    }


    // HTTP GET request
    public byte[] sendGetBinary(String url) {
        HttpURLConnection connection = null;

        byte[] data = null;
        try {

            URL obj = new URL(url);

            ByteArrayOutputStream bais = new ByteArrayOutputStream();
            InputStream is = null;
            try {
                try {
                    is = obj.openStream();
                    byte[] byteChunk = new byte[4096]; // Or whatever size you
                    // want to read in at a
                    // time.
                    int n;


                    while ((n = is.read(byteChunk)) > 0) {
                        bais.write(byteChunk, 0, n);
                    }
                } catch (OutOfMemoryError e) {
                    System.err.printf("Out of Memory", obj.toExternalForm(),
                            e.getMessage());
                    e.printStackTrace(); // TODO outofmemory handling
                    // Perform any other exception handling that's appropriate.
                }
            } catch (IOException e) {
                System.err.printf("Failed while reading bytes from %s: %s",
                        obj.toExternalForm(), e.getMessage());
                e.printStackTrace();
                // Perform any other exception handling that's appropriate.
            } finally {
                if (is != null) {
                    is.close();
                }
            }


            data = bais.toByteArray();

			/*
			 * OutputStream output = new FileOutputStream( file ); while ( true)
			 * { if (n > 0) { output.write(buffer, 0, n); } } output.close();
			 */

        } catch (Exception ex) {
            Log.e("", "ERROR IN GET REQUEST");

        } finally {

            if (connection != null) {
                connection.disconnect();
            }
        }

        return data;
    }



    // HTTP POST request
    public String sendPost(String url, String urlParameters, String host) {
        HttpURLConnection connection = null;
        String html = "";
        try {
            URL obj = new URL(url);
            connection = (HttpURLConnection) obj.openConnection();

            // add reuqest header
            connection.setRequestMethod("POST");
            connection.setRequestProperty("User-Agent", USER_AGENT);
            connection.setRequestProperty("Accept-Language", "en-US,en;q=0.5");

            connection.setRequestProperty("Content-Type",
                    "application/x-www-form-urlencoded");

            connection.setRequestProperty("Content-Length",
                    "" + Integer.toString(urlParameters.getBytes().length));
            connection.setRequestProperty("Connection:keep", "keep-alive");
            connection.setRequestProperty("Cache-Control", "no-cache");

            connection.setRequestProperty("Pragma", "no-cache");
            connection.setRequestProperty("Referer", "http://" + host);
            connection.setRequestProperty("Origin", "http://" + host);
            connection.setRequestProperty("Host", host);

            // connection.setRequestProperty("Cookie", "WSID=123456789");

            // Send post request
            connection.setDoOutput(true);
            DataOutputStream wr = new DataOutputStream(
                    connection.getOutputStream());
            wr.writeBytes(urlParameters);
            wr.flush();
            wr.close();

            // int responseCode = connection.getResponseCode();

            // System.out.println("\nSending 'POST' request to URL : " + url);
            // System.out.println("Post parameters : " + urlParameters);
            // System.out.println("Response Code : " + responseCode);

            html = IOUtils.toString(connection.getInputStream(), "UTF-8");

        } catch (Exception ex) {
            Log.e("", "ERROR IN POST REQUEST");

        } finally {

            if (connection != null) {
                connection.disconnect();
            }
        }

        return html;

    }

}
