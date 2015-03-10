package com.songbase.fm.androidapp.media;

import com.songbase.fm.androidapp.MainActivity;
import com.songbase.fm.androidapp.list.MainListElement.Action;
import com.songbase.fm.androidapp.mymusic.MyMusicController;

public class SongAction implements Action {

	public Song song;

	public SongAction(Song song) {
		this.song = song;
	}

	@Override
	public void execute() {
		MainActivity.instance.playController.playSong(song);
	}


}




