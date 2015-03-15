package com.songbase.fm.androidapp.ui.viewmode;

import java.util.ArrayList;
import java.util.List;

import com.songbase.fm.androidapp.MainActivity;
import com.songbase.fm.androidapp.list.MainListElement;
import com.songbase.fm.androidapp.mymusic.MyMusicController;
import com.songbase.fm.androidapp.ui.UIController;
import com.songbase.fm.androidapp.ui.navigationbar.NavigationBar;

public class MyMusicMode extends ViewMode {

	List<MainListElement> list = new ArrayList<MainListElement>();

	private MyMusicController myMusicController;

	public MyMusicMode() {
        super.id = UIController.MYMUSICMODE;
	}

	public void init() {
		myMusicController = new MyMusicController(list);
	}

	public void activate() {

		// Set list
		MainActivity.instance.listController.setList(list);

		// Navigate to My Music
		UIController.instance.navigationBar.navigate(
				NavigationBar.homeString, "My Playlists");

	}

	public void deactivate() {
		UIController.instance.navigationBar.navigate(
				NavigationBar.homeString, "");

        myMusicController.isSubModeActive = false;
	}

	public boolean isSubModeActive() {
		return myMusicController.isSubModeActive;
	}

	public void returnMode() {
		myMusicController.isSubModeActive = false;
		activate();

	}

}
