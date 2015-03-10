package com.songbase.fm.androidapp.ui.navigationbar;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.view.View;
import android.widget.Button;

import com.songbase.fm.androidapp.MainActivity;
import com.songbase.fm.androidapp.R;
import com.songbase.fm.androidapp.mymusic.MyMusicController;
import com.songbase.fm.androidapp.ui.UIController;

public class NavigationBar {

    private Button homeButton;

    public static String homeString = "Home";

    public NavigationBar() {

        homeButton = (Button) MainActivity.instance
                .findViewById(R.id.rootButton);

        homeButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                if (UIController.viewMode.id != UIController.MAINMODE) {
                    UIController.instance.setMode(UIController.MAINMODE);

                }

            }
        });

    }


    public void activateHome() {
        View navigationArrow = (View) MainActivity.instance
                .findViewById(R.id.navigationarrow);
        navigationArrow.setVisibility(View.GONE);

        View navigateButton = (View) MainActivity.instance
                .findViewById(R.id.navigateButton);
        navigateButton.setVisibility(View.GONE);
    }


    public void deactivateHome() {
        View navigationArrow = (View) MainActivity.instance
                .findViewById(R.id.navigationarrow);
        navigationArrow.setVisibility(View.VISIBLE);

        View navigateButton = (View) MainActivity.instance
                .findViewById(R.id.navigateButton);
        navigateButton.setVisibility(View.VISIBLE);
    }


    public void navigateBack() {
        if (UIController.viewMode
                .isSubModeActive())
            UIController.viewMode
                    .returnMode();
        else
            UIController.instance.setMode(UIController.MAINMODE);
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

        if (actionBar != null) {

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
}
