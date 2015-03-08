package com.songbase.fm.androidapp.media;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.songbase.fm.androidapp.MainActivity;
import com.songbase.fm.androidapp.list.ListAdapter;
import com.songbase.fm.androidapp.list.MainListElement;

public class PlaylistListElement extends MainListElement {

	private Playlist playlist;

	private Bitmap icon;

	public ListAdapter.ListLayout getListLayout() {
		return ListAdapter.ListLayout.NAME;
	}

	public PlaylistListElement(Playlist playlist, Action action) {
		this.playlist = playlist;
		this.action = action;

		int imageResource = MainActivity.instance.getResources().getIdentifier(
				"playlist", "drawable", MainActivity.instance.getPackageName());

		this.icon = BitmapFactory.decodeResource(
				MainActivity.instance.getResources(), imageResource);
	}

	public Bitmap getIcon() {

		return this.icon;
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
