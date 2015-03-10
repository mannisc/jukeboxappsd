package com.songbase.fm.androidapp.list;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.util.Log;

import com.songbase.fm.androidapp.MainActivity;

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

	public abstract Drawable getIcon();

    public int getIconAlpha() {
            return 255;
    }

    public Drawable getIconTop() {
        return null;
    }

	public abstract ListAdapter.ListLayout getListLayout();

}
