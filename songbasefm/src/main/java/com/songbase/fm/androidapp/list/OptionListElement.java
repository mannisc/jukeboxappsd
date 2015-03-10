package com.songbase.fm.androidapp.list;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import com.songbase.fm.androidapp.MainActivity;

public class OptionListElement extends MainListElement {

	private String name;
    public String info;

	private Drawable icon;

    public  ListAdapter.ListLayout listLayout = ListAdapter.ListLayout.NAME;


	public ListAdapter.ListLayout getListLayout() {
		return listLayout;
	}

	public OptionListElement(String name, Action action, String drawableResourceName) {
		this.name = name;
        this.info = "";

        this.action = action;

		int imageResource = MainActivity.instance.getResources().getIdentifier(
                drawableResourceName, "drawable", MainActivity.instance.getPackageName());
        this.icon =  new BitmapDrawable(MainActivity.instance.getResources(),BitmapFactory.decodeResource(
                MainActivity.instance.getResources(), imageResource));
	}

	public Drawable getIcon() {

		return this.icon;
	}

	public String getName() {
		return name;
	}

	public String getInfo() {
		return info;
	}

}
