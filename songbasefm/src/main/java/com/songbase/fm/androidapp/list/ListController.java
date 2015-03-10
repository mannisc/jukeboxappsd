package com.songbase.fm.androidapp.list;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ListView;

import com.songbase.fm.androidapp.MainActivity;
import com.songbase.fm.androidapp.R;
import com.songbase.fm.androidapp.media.SongListElement;
import com.songbase.fm.androidapp.misc.Utils;

public class ListController {

    List<MainListElement> list;

    ListAdapter listAdapter;
    FastSearchListView fastSearchListView;

    public static boolean useSections;

    public ListController(Activity activity) {

        list = new ArrayList<MainListElement>();
        useSections = false;

        fastSearchListView = (FastSearchListView) activity
                .findViewById(R.id.listView);

        listAdapter = new ListAdapter(list, activity);

        fastSearchListView.setAdapter(listAdapter);

        // React to user clicks on item
        fastSearchListView
                .setOnItemClickListener(new AdapterView.OnItemClickListener() {

                    public void onItemClick(AdapterView<?> parentAdapter,
                                            View view, int position, long id) {

                        MainListElement listElement = list.get(position);

                        listElement.executeAction();

                        // TextView clickedView = (TextView) view;

                    }
                });

        // we register for the contextmneu
        activity.registerForContextMenu(fastSearchListView);

    }


    public void onMenu(ContextMenu menu, ContextMenuInfo menuInfo) {
        AdapterContextMenuInfo aInfo = (AdapterContextMenuInfo) menuInfo;
        MainListElement listElement = listAdapter.getItem(aInfo.position);
        if (listElement instanceof SongListElement) {
            menu.setHeaderTitle(listElement.getName());

            if (!((SongListElement) listElement).getSong().isConverted || !((SongListElement) listElement).getSong().isBuffered) {
                menu.add(1, 1, 1, "Prebuffer Song");
            }
            menu.add(1, 2, 2, "Delete");
        }

    }

    public boolean onItemSelected(MenuItem item) {

        AdapterContextMenuInfo aInfo = (AdapterContextMenuInfo) item
                .getMenuInfo();

        MainListElement listElement = listAdapter.getItem(aInfo.position);

        int itemId = item.getItemId();

        switch (itemId) {
            case 1:


                Utils.debug("PREBUFFER");


                MainActivity.instance.bufferController.bufferSong(((SongListElement) listElement).getSong());


                break;
            case 2:
                //Todo delete
                list.remove(aInfo.position);
                listAdapter.notifyDataSetChanged();
                break;

            default:
                break;
        }


        return true;
    }

    public void setList(List<MainListElement> list) {
        if (list == null)
            list = new ArrayList<MainListElement>();
        this.list = list;
        listAdapter.setList(list);
    }


    public ListView getView() {
        return fastSearchListView;
    }

    public void refreshList() {
        if (listAdapter != null)
            listAdapter.notifyDataSetChanged();
    }



	/*
     * // Handle user click public void addPlanet(View view) { final Dialog d =
	 * new Dialog(this); d.setContentView(R.layout.dialog);
	 * d.setTitle("Add planet"); d.setCancelable(true);
	 * 
	 * final EditText edit = (EditText) d.findViewById(R.id.editTextPlanet);
	 * Button b = (Button) d.findViewById(R.id.button1);
	 * b.setOnClickListener(new View.OnClickListener() {
	 * 
	 * public void onClick(View v) { String planetName =
	 * edit.getText().toString(); MainActivity.this.songlist.add(new
	 * Song(planetName, 0)); MainActivity.this.aAdpt.notifyDataSetChanged(); //
	 * We notify the // data model is // changed d.dismiss(); } });
	 * 
	 * d.show(); }
	 */

}
