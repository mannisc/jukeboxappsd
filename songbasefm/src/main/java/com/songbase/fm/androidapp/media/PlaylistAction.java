package com.songbase.fm.androidapp.media;

import com.songbase.fm.androidapp.MainActivity;
import com.songbase.fm.androidapp.list.MainListElement.Action;
import com.songbase.fm.androidapp.mymusic.MyMusicController;
import com.songbase.fm.androidapp.ui.UIController;
import com.songbase.fm.androidapp.ui.navigationbar.NavigationBar;

public class PlaylistAction implements Action {

	Playlist playlist;

	public PlaylistAction(Playlist playlist) {
		this.playlist = playlist;
	}

	@Override
	public void execute() {

		MyMusicController.instance.isSubModeActive = true;

        MyMusicController.instance.loadedPlaylist = this.playlist;

		MainActivity.instance.listController.setList(this.playlist.getList());

		UIController.instance.navigationBar.navigate(NavigationBar.homeString,
				this.playlist.name);

	}
}