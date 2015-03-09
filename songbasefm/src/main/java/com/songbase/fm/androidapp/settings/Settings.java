package com.songbase.fm.androidapp.settings;

import android.content.Context;
import android.content.SharedPreferences;

public class Settings {

	public static final String SETTINGS_NAME = "SongbaseSettings";

    public static String serverURL = "http://h2406563.stratoserver.net:3001/";
            //"http://songbase.fm:3001/";
    public static String serviceServerURL = "http://h2406563.stratoserver.net:3005/";

	public static boolean isLoggedIn = false;
	public static SharedPreferences preferences;

	public static void loadSettings(Context con) {
		// Restore preferences
		preferences = con.getSharedPreferences(SETTINGS_NAME, 0);
		isLoggedIn = preferences.getBoolean("isLoggedIn", false);

	}

	public static void setIsLoggedIn(boolean isLoggedIn) {

		Settings.isLoggedIn = isLoggedIn;

		SharedPreferences.Editor editor = Settings.preferences.edit();
		editor.putBoolean("isLoggedIn", Settings.isLoggedIn);
		editor.apply();
	}
}
