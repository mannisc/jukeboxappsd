package com.songbase.fm.androidapp.ui.viewmode;

import android.view.View;
import android.widget.Button;

import java.util.ArrayList;
import java.util.List;

import com.songbase.fm.androidapp.MainActivity;
import com.songbase.fm.androidapp.R;
import com.songbase.fm.androidapp.list.ListAdapter;
import com.songbase.fm.androidapp.list.MainListElement;
import com.songbase.fm.androidapp.list.MainListElement.Action;
import com.songbase.fm.androidapp.list.OptionListElement;
import com.songbase.fm.androidapp.ui.UIController;

public class MainMode extends ViewMode {

	List<MainListElement> list = new ArrayList<MainListElement>();

    public OptionListElement currentPlaylistElement;

    public MainMode() {
		super.id = UIController.MAINMODE;
	}

	public class SearchAction implements Action {
		@Override
		public void execute() {

			UIController.instance.setMode(UIController.SEARCHMODE);

		}
	}

	public class ExploreAction implements Action {
		@Override
		public void execute() {
			UIController.instance.setMode(UIController.EXPLOREMODE);

		}
	}

	public class GenresAction implements Action {
		@Override
		public void execute() {
			UIController.instance.setMode(UIController.GENRESMODE);

		}
	}

	public class MyMusicAction implements Action {
		@Override
		public void execute() {
			UIController.instance.setMode(UIController.MYMUSICMODE);
		}
	}

    public class CurrentPlaylistAction implements Action {
        @Override
        public void execute() {
            UIController.instance.setMode(UIController.CURRENTPLAYLISTMODE);
        }
    }

	public void init() {

		list.add(new OptionListElement("Search", new SearchAction(),"search"));

		//list.add(new OptionListElement("Explore", new ExploreAction(),""));

		//list.add(new OptionListElement("Genres", new GenresAction(),""));

		list.add(new OptionListElement("My Playlists", new MyMusicAction(),"playlist"));


        currentPlaylistElement = new OptionListElement("Current Playlist" , new CurrentPlaylistAction(),"currentplay");

        list.add(currentPlaylistElement);


    }

	public void activate() {

		MainActivity.instance.listController.setList(list);


        UIController.instance.navigationBar.activateHome();



    }

	public void deactivate() {

        UIController.instance.navigationBar.deactivateHome();

	}

}
