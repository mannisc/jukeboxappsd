package com.songbase.fm.androidapp.media;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import com.songbase.fm.androidapp.MainActivity;
import com.songbase.fm.androidapp.list.ListAdapter;
import com.songbase.fm.androidapp.list.MainListElement;

public class PlaylistListElement extends MainListElement {

	private Playlist playlist;

	public ListAdapter.ListLayout getListLayout() {
		return ListAdapter.ListLayout.NAME;
	}

	public PlaylistListElement(Playlist playlist, Action action) {
		this.playlist = playlist;
		this.action = action;
	}

	public Drawable getIcon() {
		return playlist.getIcon();
	}

	public String getName() {
		return playlist.name;
	}

	public String getInfo() {
		return "";
	}

    public Playlist getPlaylist(){
        return playlist;
    };
}
