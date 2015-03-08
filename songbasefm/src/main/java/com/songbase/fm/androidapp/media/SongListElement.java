package com.songbase.fm.androidapp.media;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.songbase.fm.androidapp.MainActivity;
import com.songbase.fm.androidapp.list.ListAdapter;
import com.songbase.fm.androidapp.list.MainListElement;

public class SongListElement extends MainListElement {

	private Song song;

	private Bitmap icon;

	public ListAdapter.ListLayout getListLayout() {
		return ListAdapter.ListLayout.NAMEINFO;
	}

	public SongListElement(Song song, Action action) {
		this.song = song;
		this.action = action;

		int imageResource = MainActivity.instance.getResources().getIdentifier(
				"music", "drawable", MainActivity.instance.getPackageName());
		this.icon = BitmapFactory.decodeResource(
				MainActivity.instance.getResources(), imageResource);
	}

	public Bitmap getIcon() {

		return this.icon;
	}

	public String getName() {
		return song.name;
	}

	public String getInfo() {
		return song.getArtistName();
	}

    public Song getSong(){
        return song;
    }

}
