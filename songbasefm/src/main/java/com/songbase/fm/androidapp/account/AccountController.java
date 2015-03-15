package com.songbase.fm.androidapp.account;

import java.util.List;

import org.json.JSONObject;

import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.songbase.fm.androidapp.MainActivity;
import com.songbase.fm.androidapp.authentication.AuthController;
import com.songbase.fm.androidapp.authentication.RSAUtils;
import com.songbase.fm.androidapp.list.MainListElement;
import com.songbase.fm.androidapp.media.PlaylistListElement;
import com.songbase.fm.androidapp.mymusic.MyMusicController;
import com.songbase.fm.androidapp.persistence.PersistenceController;
import com.songbase.fm.androidapp.settings.Settings;

public class AccountController {

    // Login
    public static AQuery aQuery;

    public static AccountController instance = null;

    private static UserAutoLoginTask mAuthTask = null;

    public static long requestid = 1;

    public AccountController() {
        aQuery = MainActivity.instance.aQuery;

        instance = this;
    }

    public void singInAuto() {

        String url = Settings.serverURL + "init.js?nocache="
                + ((Integer) (int) (Math.random() * 100000000)).toString();

        AjaxCallback<String> cb = new AjaxCallback<String>() {

            @Override
            public void callback(String url, String string, AjaxStatus status) {

                if (string != null&&string.contains("\"auth\":\"true\"")) {
                    mAuthTask = new UserAutoLoginTask(AuthController.loginToken);
                    mAuthTask.execute((Void) null);
                //Auto login failed
                }else{
                    Settings.setIsLoggedIn(false);
                    Intent intent = new Intent(MainActivity.instance, LoginActivity.class);
                    MainActivity.instance.startActivity(intent);
                }

            }
        };

        cb.url(url).type(String.class);
        cb.encoding("UTF-16LE");
        cb.header("Referer", "songbase.fm");


        aQuery.ajax(cb);

        Log.e("AUTO LOGIN","!!................");
        Log.e("AUTO LOGIN",url);




    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UserAutoLoginTask extends AsyncTask<Void, Void, Boolean> {

        private final String mLoginToken;

        UserAutoLoginTask(String loginToken) {
            mLoginToken = loginToken;

        }

        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.

            Log.e("LOGINTOKEN+++++ Async", mLoginToken);

            try {

                if (!tryAutoLogin())
                    return false;

            } catch (Exception e) {
                Log.e("xxxxxxx", "-------------------");
                e.printStackTrace();
                Log.e("xxxxxxx", "-------------------");

                return false;
            }

            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {

            if (success) {
                Settings.setIsLoggedIn(true);

                AccountController.instance.loadStoredOnlineData();
            }
        }

        @Override
        protected void onCancelled() {

        }

        private boolean tryAutoLogin() throws Exception {

            String loginToken = "";
            if (mLoginToken != null)
                loginToken = RSAUtils.encrypt(mLoginToken);

            String url = Settings.serverURL + "?loginToken=" + loginToken
                    + "&auth=" + AuthController.ip_token;

            AjaxCallback<String> cb = new AjaxCallback<String>();



            cb.url(url).type(String.class);
            cb = cb.encoding("UTF-16LE");//
            cb.header("Referer", "songbase.fm");
            // US-ASCII,windows-1252

            Log.e("LOGIN",url);

            aQuery.sync(cb);
            String response = cb.getResult();

            AjaxStatus status = cb.getStatus();
            if (response != null) {

                Log.e("loginToken", AuthController.loginToken);

                return true;

            } else {

                // ajax error, show error code
                Log.e("AJAX ERROR", "Error:" + status.getCode());
                return false;
            }

        }
    }

    public void loadStoredOnlineData() {

        aQuery = MainActivity.instance.aQuery;

        // Load data from Server
        AccountController.requestid = AccountController.requestid + 1;
        long nonce = AccountController.requestid;

        String savetoken = RSAUtils.encrypt(AuthController.loginToken + nonce);

        String url = Settings.serverURL + "?getdatalist=" + savetoken + "&n="
                + nonce + "&type=playlist&auth=" + AuthController.ip_token;

        Log.d("loadStoredOnlineData",url);//TODO remove

        AjaxCallback<JSONObject> cb = new AjaxCallback<JSONObject>() {

            @Override
            public void callback(String url, JSONObject json, AjaxStatus status) {

                /**
                 * TODO REMOVE
                 * MOCKING LOGIN AND GETTING DATA
                 *+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

                 try {

                 json = new JSONObject("{\"items\": [{ \"name\": \"Test Playlist\",\"data\": [{\"name\": \"Love Me Like You Do\", \"isVideoUrlLoaded\":\"true\", ,  \"videoURL\":\"true\" , \"artist\": {\"name\": \"Ellie Goulding\"}}]}]}");

                 } catch (JSONException e) {
                 e.printStackTrace();
                 }

                 //+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++


                 */
                if (json != null) {

                    Log.e("Playlists:",json.toString());
                    List<PlaylistListElement> list = MyMusicController.getPlaylistsFromJSON(json);

                    MyMusicController.instance.setPlaylistList(list);

                    List<MainListElement> songs = PersistenceController.instance.loadPlayedSong();
                    MyMusicController.instance.setPlayedSongs(songs);

                    PersistenceController.instance.saveOfflineData();


                } else {
                    Toast.makeText(aQuery.getContext(),
                            "Error:" + status.getCode(), Toast.LENGTH_LONG)
                            .show();
                }
            }

        };

        cb = cb.encoding("UTF-16LE");
        cb.header("Referer", "songbase.fm");
        aQuery.ajax(url, JSONObject.class,-1, cb);

    }
}
