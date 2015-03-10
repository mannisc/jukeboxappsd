package com.songbase.fm.androidapp.ui.viewmode;

import java.util.ArrayList;
import java.util.List;

import com.songbase.fm.androidapp.MainActivity;
import com.songbase.fm.androidapp.list.MainListElement;
import com.songbase.fm.androidapp.ui.UIController;
import com.songbase.fm.androidapp.ui.navigationbar.NavigationBar;

public class GenresMode extends ViewMode {

	List<MainListElement> list = new ArrayList<MainListElement>();

	public GenresMode() {
		super.id = 3;
	}

	public void activate() {

		// Set list
		MainActivity.instance.listController.setList(list);

		// Navigate to Genres
		UIController.instance.navigationBar.navigate(
				NavigationBar.homeString, "Genres");

	}

	public void deactivate() {

		UIController.instance.navigationBar.navigate(
				NavigationBar.homeString, "");

	}
}
