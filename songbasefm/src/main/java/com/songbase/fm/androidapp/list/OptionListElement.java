package com.songbase.fm.androidapp.list;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.songbase.fm.androidapp.MainActivity;

public class OptionListElement extends MainListElement {

	private String name;

	private Bitmap icon;

	public ListAdapter.ListLayout getListLayout() {
		return ListAdapter.ListLayout.NAME;
	}

	public OptionListElement(String name, Action action, String drawableResourceName) {
		this.name = name;
		this.action = action;

		int imageResource = MainActivity.instance.getResources().getIdentifier(
                drawableResourceName, "drawable", MainActivity.instance.getPackageName());
		this.icon = BitmapFactory.decodeResource(
				MainActivity.instance.getResources(), imageResource);
	}

	public Bitmap getIcon() {

		return this.icon;
	}

	public String getName() {
		return name;
	}

	public String getInfo() {
		return "";
	}

}
