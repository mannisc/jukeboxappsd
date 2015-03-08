package com.songbase.fm.androidapp.ui.viewmode;

import java.util.ArrayList;
import java.util.List;

import com.songbase.fm.androidapp.MainActivity;
import com.songbase.fm.androidapp.list.MainListElement;
import com.songbase.fm.androidapp.list.MainListElement.Action;
import com.songbase.fm.androidapp.list.OptionListElement;

public class MainMode extends ViewMode {

	List<MainListElement> list = new ArrayList<MainListElement>();

	public MainMode() {
		super.id = 0;
	}

	public class SearchAction implements Action {
		@Override
		public void execute() {

			MainActivity.instance.uiController.setMode(1);

		}
	}

	public class ExploreAction implements Action {
		@Override
		public void execute() {
			MainActivity.instance.uiController.setMode(2);

		}
	}

	public class GenresAction implements Action {
		@Override
		public void execute() {
			MainActivity.instance.uiController.setMode(3);

		}
	}

	public class MyMusicAction implements Action {
		@Override
		public void execute() {
			MainActivity.instance.uiController.setMode(4);

		}
	}

	public void init() {

		list.add(new OptionListElement("Search", new SearchAction(),"search"));

		//list.add(new OptionListElement("Explore", new ExploreAction()));

		//list.add(new OptionListElement("Genres", new GenresAction()));

		list.add(new OptionListElement("My Playlists", new MyMusicAction(),"playlist"));

	}

	public void activate() {

		MainActivity.instance.listController.setList(list);

	}

	public void deactivate() {

	}

}
