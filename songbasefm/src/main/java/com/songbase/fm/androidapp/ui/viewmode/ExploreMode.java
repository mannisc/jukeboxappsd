package com.songbase.fm.androidapp.ui.viewmode;

import java.util.ArrayList;
import java.util.List;

import com.songbase.fm.androidapp.MainActivity;
import com.songbase.fm.androidapp.list.MainListElement;
import com.songbase.fm.androidapp.ui.navigationbar.NavigationBar;

public class ExploreMode extends ViewMode {

	List<MainListElement> list = new ArrayList<MainListElement>();

	public ExploreMode() {
		super.id = 2;
	}

	public void activate() {

		// Set list
		MainActivity.instance.listController.setList(list);

		// Navigate to Explore
		MainActivity.instance.uiController.navigationBar.navigate(
				NavigationBar.homeString, "Explore");
	}

	public void deactivate() {

		MainActivity.instance.uiController.navigationBar.navigate(
				NavigationBar.homeString, "");

	}

}