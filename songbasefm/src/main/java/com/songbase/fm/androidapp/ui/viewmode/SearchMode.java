package com.songbase.fm.androidapp.ui.viewmode;

import java.util.ArrayList;
import java.util.List;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;

import com.songbase.fm.androidapp.MainActivity;
import com.songbase.fm.androidapp.R;
import com.songbase.fm.androidapp.list.FastSearchListView;
import com.songbase.fm.androidapp.list.ListController;
import com.songbase.fm.androidapp.list.MainListElement;
import com.songbase.fm.androidapp.search.DelayedTextWatcher;
import com.songbase.fm.androidapp.search.SearchController;
import com.songbase.fm.androidapp.ui.UIController;
import com.songbase.fm.androidapp.ui.navigationbar.NavigationBar;

public class SearchMode extends ViewMode {

	List<MainListElement> list = new ArrayList<MainListElement>();

	private SearchController searchController;

	public SearchMode() {
        super.id = UIController.SEARCHMODE;
	}

	public void init() {

		searchController = new SearchController(list);

		EditText searchText = (EditText) MainActivity.instance
				.findViewById(R.id.searchText);

		searchText.addTextChangedListener(
                new DelayedTextWatcher(350) {

                    @Override
                    public void afterTextChangedDelayed(Editable s) {
                        searchController.search(s.toString());

                    }
                }
        );

	}

	public void activate() {
		ListController.useSections = false;
		// Change Layout
		// Set List above search
		FastSearchListView viewToLayout = (FastSearchListView) MainActivity.instance
				.findViewById(R.id.listView);

		RelativeLayout.LayoutParams p = (LayoutParams) viewToLayout
				.getLayoutParams();

		p.addRule(RelativeLayout.ABOVE, R.id.searchText);

		// Show Search field and focus
		EditText searchText = (EditText) MainActivity.instance
				.findViewById(R.id.searchText);

		searchText.setVisibility(View.VISIBLE);
		searchText.requestFocus();

		UIController.instance.showKeyboard();

		// Set list
		MainActivity.instance.listController.setList(list);

		// Navigate to Search

		UIController.instance.navigationBar.navigate(
				NavigationBar.homeString, "Search");

        MainActivity.instance.listController.getView().setStackFromBottom(false);

	}

	public void deactivate() {



		ListController.useSections = false;

        MainActivity.instance.listController.getView().setStackFromBottom(true);

        UIController.instance.hideKeyboard();



		// Change Layout
		FastSearchListView viewToLayout = (FastSearchListView) MainActivity.instance
				.findViewById(R.id.listView);

		RelativeLayout.LayoutParams p = (LayoutParams) viewToLayout
				.getLayoutParams();

		p.addRule(RelativeLayout.ABOVE, R.id.navigationBar);

		EditText searchText = (EditText) MainActivity.instance
				.findViewById(R.id.searchText);
		searchText.setVisibility(View.GONE);

		UIController.instance.navigationBar.navigate(
				NavigationBar.homeString, "");

	}
}
