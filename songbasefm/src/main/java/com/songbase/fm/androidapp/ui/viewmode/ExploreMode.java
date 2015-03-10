package com.songbase.fm.androidapp.ui.viewmode;

import java.util.ArrayList;
import java.util.List;

import com.songbase.fm.androidapp.MainActivity;
import com.songbase.fm.androidapp.list.MainListElement;
import com.songbase.fm.androidapp.ui.UIController;
import com.songbase.fm.androidapp.ui.navigationbar.NavigationBar;

public class ExploreMode extends ViewMode {

    List<MainListElement> list = new ArrayList<MainListElement>();

    public ExploreMode() {
        super.id = UIController.EXPLOREMODE;
    }

    public void activate() {

        // Set list
        MainActivity.instance.listController.setList(list);

        // Navigate to Explore
        UIController.instance.navigationBar.navigate(
                NavigationBar.homeString, "Explore");
    }

    public void deactivate() {

        UIController.instance.navigationBar.navigate(
                NavigationBar.homeString, "");

    }

}
