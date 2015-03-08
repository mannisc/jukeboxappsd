package com.songbase.fm.androidapp.ui.navigationbar;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.view.View;
import android.widget.Button;

import com.songbase.fm.androidapp.MainActivity;
import com.songbase.fm.androidapp.R;

public class NavigationBar {

	private Button homeButton;

	public static String homeString = "Home";

	public NavigationBar() {

		homeButton = (Button) MainActivity.instance
				.findViewById(R.id.rootButton);

		homeButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {

				if (MainActivity.instance.uiController.viewMode.id != 0) {
					if (MainActivity.instance.uiController.viewMode
							.isSubModeActive())
						MainActivity.instance.uiController.viewMode
								.returnMode();
					else
						MainActivity.instance.uiController.setMode(0);

				}

			}
		});

	}

	@SuppressWarnings("deprecation")
	@SuppressLint("NewApi")
	public void navigate(String nameRoot, String name) {

		Button buttonRoot = (Button) MainActivity.instance
				.findViewById(R.id.rootButton);
		buttonRoot.setText(nameRoot);

		Button buttonNavigation = (Button) MainActivity.instance
				.findViewById(R.id.navigateButton);
		buttonNavigation.setText(name);

		View view = (View) MainActivity.instance
				.findViewById(R.id.navigationarrow);

		ActionBar actionBar = MainActivity.instance.getActionBar();

		if (name.equals("")) {

			// actionBar.setDisplayHomeAsUpEnabled(false);
			actionBar.show();

			if (Integer.valueOf(android.os.Build.VERSION.SDK) >= 16)
				view.setBackground(MainActivity.instance.getResources()
						.getDrawable(R.drawable.bgnavigation));
			else
				view.setBackgroundDrawable(MainActivity.instance.getResources()
						.getDrawable(R.drawable.bgnavigation));

		} else {

			// actionBar.setDisplayHomeAsUpEnabled(true);
			actionBar.hide();

			if (Integer.valueOf(android.os.Build.VERSION.SDK) >= 16)
				view.setBackground(MainActivity.instance.getResources()
						.getDrawable(R.drawable.navigationarrow));
			else
				view.setBackgroundDrawable(MainActivity.instance.getResources()
						.getDrawable(R.drawable.navigationarrow));

		}

	}
}
