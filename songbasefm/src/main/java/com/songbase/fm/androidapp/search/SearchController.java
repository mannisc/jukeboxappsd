package com.songbase.fm.androidapp.search;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.songbase.fm.androidapp.MainActivity;
import com.songbase.fm.androidapp.list.MainListElement;
import com.songbase.fm.androidapp.media.Song;
import com.songbase.fm.androidapp.media.SongAction;
import com.songbase.fm.androidapp.media.SongListElement;
import com.songbase.fm.androidapp.ui.UIController;

public class SearchController {

    List<MainListElement> list = new ArrayList<MainListElement>();

    public AQuery aQuery;
    public String lastfmapikey = "019c7bcfc5d37775d1e7f651d4c08e6f";
    public static String searchUrl = "";

    public SearchController(List<MainListElement> list) {
        this.list = list;
        aQuery = MainActivity.instance.aQuery;
    }

    public void search(String searchText) {
        // perform a Google search in just a few lines of code

        if (searchText.equals("")) {
            list.clear();
            MainActivity.instance.listController.setList(list);
        } else {

            String url = "http://ws.audioscrobbler.com/2.0/?method=track.search&track="
                    + searchText
                    + "&limit=100&api_key="
                    + lastfmapikey
                    + "&format=json";

            SearchController.searchUrl = url;
            aQuery.ajax(url, JSONObject.class, new AjaxCallback<JSONObject>() {


                @Override
                public void callback(String localUrl, JSONObject json, AjaxStatus status) {
                    // status.getCode()

                    Log.e("SEARCHRES", "----");
                    if (json != null)
                        Log.e("RESULTS", json.toString());


                    if (localUrl.equals(SearchController.searchUrl)) {

                        if (json != null) {
                            list.clear();

                            try { //In case of empty search json throws error
                                if (UIController.instance.isModeActive(UIController.SEARCHMODE)) {
                                    JSONArray trackmatches = json
                                            .getJSONObject("results")
                                            .getJSONObject("trackmatches")
                                            .getJSONArray("track");
                                    for (int i = 0; i < trackmatches.length(); i++) {
                                        JSONObject trackJSON = trackmatches
                                                .getJSONObject(i);
                                        Song song = new Song(trackJSON.getString("name"),
                                                trackJSON.getString("artist"));



                                        try {
                                            String image =  ((JSONObject)trackJSON.getJSONArray("image").get(2)).getString("#text");
                                            List<Song> songsIcon = new ArrayList<Song>();
                                            songsIcon.add(song);
                                            Song.loadIcon(image,songsIcon);

                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }


                                        list.add(new SongListElement(song,
                                                new SongAction(song)));
                                    }
                                    // Set list
                                    MainActivity.instance.listController.setList(list);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            MainActivity.instance.listController.setList(list);
                        } else {
                            Log.e("AJAX ERROR", "Error:" + status.getCode());
                        }

                    }
                }
            });

        }


    }

}
