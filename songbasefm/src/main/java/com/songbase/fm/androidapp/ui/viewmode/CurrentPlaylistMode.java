package com.songbase.fm.androidapp.ui.viewmode;

import com.songbase.fm.androidapp.MainActivity;
import com.songbase.fm.androidapp.list.MainListElement;
import com.songbase.fm.androidapp.media.Playlist;
import com.songbase.fm.androidapp.media.PlaylistListElement;
import com.songbase.fm.androidapp.mymusic.MyMusicController;
import com.songbase.fm.androidapp.playing.PlayController;
import com.songbase.fm.androidapp.ui.UIController;
import com.songbase.fm.androidapp.ui.navigationbar.NavigationBar;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Manfred on 09.03.2015.
 */

public class CurrentPlaylistMode extends ViewMode {

    List<MainListElement> list = new ArrayList<MainListElement>();

    public CurrentPlaylistMode() {
        super.id = UIController.CURRENTPLAYLISTMODE;
    }

    public void activate() {

        String playlistName = "Current Playlist";
        list = new ArrayList<MainListElement>();

        Playlist playlist = MainActivity.instance.playController.getActivePlaylist();

        if (playlist != null) {
            list = playlist.getList();
            playlistName = playlist.getName();
        }

        MainActivity.instance.listController.setList(list);

        // Navigate to Explore
        UIController.instance.navigationBar.navigate(
                NavigationBar.homeString, playlistName);
    }

    public void deactivate() {

        UIController.instance.navigationBar.navigate(
                NavigationBar.homeString, "");

    }

}
