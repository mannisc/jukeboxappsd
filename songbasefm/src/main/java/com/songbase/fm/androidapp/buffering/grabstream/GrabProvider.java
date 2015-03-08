package com.songbase.fm.androidapp.buffering.grabstream;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.util.Log;

import java.io.IOException;
import java.net.Socket;

/**
 * Created by Manfred on 27.02.2015.
 */
public class GrabProvider extends ContentProvider {
    @Override
    public boolean onCreate() {
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] strings, String s, String[] strings2, String s2) {


        Log.e("Uri Call recieved!!!!", uri.toString());


        return null;
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        return null;
    }

    @Override
    public int delete(Uri uri, String s, String[] strings) {
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String s, String[] strings) {
        return 0;
    }

    public AssetFileDescriptor openAssetFile(Uri uri, String mode) {
        Log.e("AssetFileDescriptor", mode + "   " + uri.toString());

        AssetFileDescriptor afd = null;


        GrabSocket socket = new GrabSocket("http://h2406563.stratoserver.net/mustang.mp4");


        afd = new AssetFileDescriptor(ParcelFileDescriptor.fromSocket(socket), 0, AssetFileDescriptor.UNKNOWN_LENGTH);


        return afd;
    }

    public ParcelFileDescriptor openFile(Uri uri, String mode) {
        Log.e("UParcelFileDescriptor", mode + "   " + uri.toString());

        return null;
    }


}
