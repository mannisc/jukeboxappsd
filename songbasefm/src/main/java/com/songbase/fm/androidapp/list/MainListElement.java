package com.songbase.fm.androidapp.list;

import android.graphics.Bitmap;
import android.util.Log;

public abstract class MainListElement {

	protected Action action;

	public interface Action {
		public void execute();
	}

	public void executeAction() {
		if (action != null) {

			action.execute();

		}
	}

	public String getName() {
		return null;

	}

	public abstract String getInfo();

	public abstract Bitmap getIcon();

	public abstract ListAdapter.ListLayout getListLayout();

}
