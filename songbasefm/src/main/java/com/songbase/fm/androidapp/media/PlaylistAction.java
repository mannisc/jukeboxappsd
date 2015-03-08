package com.songbase.fm.androidapp.media;

import com.songbase.fm.androidapp.MainActivity;
import com.songbase.fm.androidapp.list.MainListElement.Action;
import com.songbase.fm.androidapp.mymusic.MyMusicController;

public class PlaylistAction implements Action {

	Playlist playlist;

	public PlaylistAction(Playlist playlist) {
		this.playlist = playlist;
	}

	@Override
	public void execute() {

		MyMusicController.instance.isSubModeActive = true;

        MyMusicController.instance.activePlaylistGid = this.playlist.getGid();

		MainActivity.instance.listController.setList(this.playlist.getList());

		MainActivity.instance.uiController.navigationBar.navigate("My Playlists",
				this.playlist.name);

	}
}