package com.songbase.fm.androidapp.media;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.view.Gravity;

import com.songbase.fm.androidapp.MainActivity;
import com.songbase.fm.androidapp.R;
import com.songbase.fm.androidapp.list.ListAdapter;
import com.songbase.fm.androidapp.list.MainListElement;

public class SongListElement extends MainListElement {

    private static Drawable playIcon;

    private Song song;

    public ListAdapter.ListLayout getListLayout() {
        return ListAdapter.ListLayout.NAMEINFO;
    }

    public SongListElement(Song song, Action action) {
        this.song = song;
        this.action = action;

    }

    public Drawable getIcon() {
        return song.getIcon();
    }


    @Override
    public int getIconAlpha() {
        if (MainActivity.instance.playController.isPlayingSong(song))
            return 180;
        else
            return 255;


    }


    @Override
    public Drawable getIconTop() {
        if (MainActivity.instance.playController.isPlayingSong(song)) {
            if (playIcon == null) {
                int imageResource = MainActivity.instance.getResources().getIdentifier(
                        "currentplay", "drawable", MainActivity.instance.getPackageName());
                playIcon = new BitmapDrawable(MainActivity.instance.getResources(), BitmapFactory.decodeResource(
                        MainActivity.instance.getResources(), imageResource));
            }
            return playIcon;
        } else return null;
    }

    public String getName() {
        return song.name;
    }

    public String getInfo() {
        return song.getInfo();
    }


    public Song getSong() {
        return song;
    }

}
