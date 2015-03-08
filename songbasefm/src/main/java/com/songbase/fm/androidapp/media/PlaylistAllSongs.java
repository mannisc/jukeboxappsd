package com.songbase.fm.androidapp.media;

import com.songbase.fm.androidapp.list.MainListElement;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Manfred on 02.03.2015.
 */
public class PlaylistAllSongs extends Playlist {

    public PlaylistAllSongs(String name){
        super(name, "0", null);
    }

    public PlaylistAllSongs(String name, String gid, List<MainListElement> list) {
        super(name, "0", null);
    }



    @Override
    public List<MainListElement> getList() {
        return new ArrayList<MainListElement>();
    }

}
