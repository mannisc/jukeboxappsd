package com.songbase.fm.androidapp.ui.viewmode;

import java.util.ArrayList;
import java.util.List;

import com.songbase.fm.androidapp.MainActivity;
import com.songbase.fm.androidapp.list.MainListElement;
import com.songbase.fm.androidapp.mymusic.MyMusicController;
import com.songbase.fm.androidapp.ui.navigationbar.NavigationBar;

public class MyMusicMode extends ViewMode {

	List<MainListElement> list = new ArrayList<MainListElement>();

	private MyMusicController myMusicController;

	public MyMusicMode() {
		super.id = 4;
	}

	public void init() {
		myMusicController = new MyMusicController(list);
	}

	public void activate() {

		// Set list
		MainActivity.instance.listController.setList(list);

		// Navigate to My Music
		MainActivity.instance.uiController.navigationBar.navigate(
				NavigationBar.homeString, "My Playlists");

	}

	public void deactivate() {
		MainActivity.instance.uiController.navigationBar.navigate(
				NavigationBar.homeString, "");

	}

	public boolean isSubModeActive() {
		return myMusicController.isSubModeActive;
	}

	public void returnMode() {
		myMusicController.isSubModeActive = false;
		activate();

	}

}
