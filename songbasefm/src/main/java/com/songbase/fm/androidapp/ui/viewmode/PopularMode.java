package com.songbase.fm.androidapp.ui.viewmode;

import com.songbase.fm.androidapp.MainActivity;
import com.songbase.fm.androidapp.list.MainListElement;
import com.songbase.fm.androidapp.media.Playlist;
import com.songbase.fm.androidapp.mymusic.MyMusicController;
import com.songbase.fm.androidapp.popular.PopularController;
import com.songbase.fm.androidapp.ui.UIController;
import com.songbase.fm.androidapp.ui.navigationbar.NavigationBar;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Manfred on 14.03.2015.
 */
public class PopularMode extends ViewMode {


    private PopularController popularController;

    List<MainListElement> list = new ArrayList<MainListElement>();


    public PopularMode() {
        super.id = UIController.POPULARMODE;
    }

    public void init() {
        popularController = new PopularController(list);
    }

    public void activate() {


    //    Playlist playlist = MainActivity.instance.playController.getActivePlaylist();

        popularController.getPopularSongs(false);


        MainActivity.instance.listController.setList(list);

        // Navigate to Explore
        UIController.instance.navigationBar.navigate(
                NavigationBar.homeString, "Popular Songs");

    }

    public void deactivate() {

        UIController.instance.navigationBar.navigate(
                NavigationBar.homeString, "");

    }


}
