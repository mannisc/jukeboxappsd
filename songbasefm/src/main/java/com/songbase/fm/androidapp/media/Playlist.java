package com.songbase.fm.androidapp.media;

import java.util.ArrayList;
import java.util.List;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import com.songbase.fm.androidapp.MainActivity;
import com.songbase.fm.androidapp.list.ListAdapter;
import com.songbase.fm.androidapp.list.MainListElement;

public class Playlist extends MainListElement {


    public String name;

    public String gid;

    List<MainListElement> list = new ArrayList<MainListElement>();

    private Drawable icon;

    public ListAdapter.ListLayout getListLayout() {
        return ListAdapter.ListLayout.NAME;
    }

    public Playlist(String name, String gid,List<MainListElement> list) {

        this.name = name;
        this.gid = gid;
        this.list = list;

        int imageResource = MainActivity.instance.getResources().getIdentifier(
                "playlist", "drawable", MainActivity.instance.getPackageName());
        this.icon =  new BitmapDrawable(MainActivity.instance.getResources(),BitmapFactory.decodeResource(
                MainActivity.instance.getResources(), imageResource));

    }

    public Drawable getIcon() {

        return this.icon;
    }

    public String getName() {
        return name;
    }

    public List<MainListElement> getList() {
        return list;
    }

    /**
     * Get Info to Songs displayed in List
     */

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getInfo() {
        return "";
    }

    public String getGid() {
        return gid;
    }
}