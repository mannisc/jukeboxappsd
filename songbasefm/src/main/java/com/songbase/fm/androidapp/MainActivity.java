package com.songbase.fm.androidapp;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuItem;
import android.view.View;

import com.androidquery.AQuery;
import com.songbase.fm.androidapp.account.AccountController;
import com.songbase.fm.androidapp.account.LoginActivity;
import com.songbase.fm.androidapp.authentication.AuthController;
import com.songbase.fm.androidapp.authentication.RSAUtils;
import com.songbase.fm.androidapp.buffering.BufferController;
import com.songbase.fm.androidapp.list.ListController;
import com.songbase.fm.androidapp.misc.Utils;
import com.songbase.fm.androidapp.persistence.PersistenceController;
import com.songbase.fm.androidapp.playing.PlayController;
import com.songbase.fm.androidapp.settings.Settings;
import com.songbase.fm.androidapp.ui.UIController;



public class MainActivity extends Activity  {

    public static MainActivity instance;

    public PersistenceController persistenceController;
    public AccountController accountController;
    public UIController uiController;
    public ListController listController;
    public PlayController playController;

    public BufferController bufferController;
    public AQuery aQuery;
    public boolean destroyed = false;

    public boolean loaded = false;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        instance = this;
        destroyed = false;


        RSAUtils.init();

        // Load Settings
        Settings.loadSettings(instance);

        // AuthController

        AuthController.init(instance);

        Log.e("TOKEN: ", AuthController.loginToken);


        aQuery = new AQuery(this);

        listController = new ListController(this);

        uiController = new UIController(this);

        playController = new PlayController(this);

        bufferController = new BufferController();

        accountController = new AccountController();

        persistenceController = new PersistenceController(this);


        //Offline, load offline Data if already user, elsewhise ask to create account

        persistenceController.loadStoredOfflineData();

        if (Utils.checkNetworkStatus() != 0) { //&&ettings.isLoggedIn Todo soll auch offline nicht einloggen wenn nicht angemeldet davor
            // Settings.setIsLoggedIn(false);

            // Nicht bereits eingeloggt
            if (!Settings.isLoggedIn || (AuthController.loginToken == null|| AuthController.loginToken.equals(""))){
                Intent intent = new Intent(this, LoginActivity.class);
                startActivity(intent);
            } else {
                // Auto login
                accountController.singInAuto();
            }
        }

        this.loaded = true;
    }

    @Override
    protected void onStart() {
        super.onStart(); // Always call the superclass method first
        Log.e("onStart: ", AuthController.loginToken);

    }


    @Override
    protected void onPause() {
        super.onPause(); // Always call the superclass method first

    }


    @Override
    protected void onStop() {
        super.onStop(); // Always call the superclass method first

    }

    @Override
    protected void onDestroy() {
        super.onDestroy(); // Always call the superclass method first
        destroyed = true;
        instance = null;

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (uiController != null)
            uiController.onConfigurationChanged();
    }

    // We want to create a context Menu when the user long click on an item
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
         listController.onMenu(menu, menuInfo);

    }

    // This method is called when user selects an Item in the Context menu
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        return listController.onItemSelected(item);

    }

    // 2.0 and above
    @Override
    public void onBackPressed() {

        if (UIController.viewMode.id == UIController.MAINMODE) {
            moveTaskToBack(true);
        } else {// Not Main Mode
            UIController.instance.navigationBar.navigateBack();
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                uiController.setMode(0);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


}
