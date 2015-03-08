package com.songbase.fm.androidapp.buffering;

import com.songbase.fm.androidapp.media.Song;
import com.songbase.fm.androidapp.misc.CustomCallback;

public interface Vid2mp3Converter {

	public boolean bufferSong(Song song,String streamUrl,String videoUrl, CustomCallback callback);

}
