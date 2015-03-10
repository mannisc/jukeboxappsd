package com.songbase.fm.androidapp.playing;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Messenger;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.gson.Gson;
import com.songbase.fm.androidapp.MainActivity;
import com.songbase.fm.androidapp.R;
import com.songbase.fm.androidapp.list.ListAdapter;
import com.songbase.fm.androidapp.list.MainListElement;
import com.songbase.fm.androidapp.list.OptionListElement;
import com.songbase.fm.androidapp.media.Playlist;
import com.songbase.fm.androidapp.media.PlaylistListElement;
import com.songbase.fm.androidapp.media.Song;
import com.songbase.fm.androidapp.media.SongListElement;
import com.songbase.fm.androidapp.misc.CustomCallback;
import com.songbase.fm.androidapp.misc.Utils;
import com.songbase.fm.androidapp.mymusic.MyMusicController;
import com.songbase.fm.androidapp.persistence.PersistenceController;
import com.songbase.fm.androidapp.playing.service.MyMediaPlayerService;
import com.songbase.fm.androidapp.playing.service.ServiceMessageHandler;
import com.songbase.fm.androidapp.ui.UIController;
import com.songbase.fm.androidapp.ui.viewmode.MainMode;

import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.util.List;

public class PlayController {

    // Main Activity
    public Activity activity;

    public static PlayController instance;

    public boolean isPlaying = false;//Playing State is active, even if song is not loaded yet
    public boolean isLoaded = false;//Song is Loaded in MediaPlayer
    public boolean isStarted = false;//Last Song is started


    public Song activeSong = null;
    public Playlist activePlaylist = null;
    public String activePlaylistGid = "";


    public int currentDuration = 0;
    public int currentPosition = 0;

    public long playCounter = 0;


    public Handler playUpdateHandler = null;

    public Thread playUpdateThread = null;

    public static Gson gson = new Gson();

    public ImageView coverImage;
    public TextView infoText;
    public TextView durationText;
    public TextView currentPositionText;

    public static Handler messageHandler;

    public PlayController(Activity activity) {

        instance = this;

        this.activity = activity;

        playUpdateHandler = new Handler(activity.getMainLooper());

        messageHandler = new ServiceMessageHandler();

        coverImage = (ImageView) MainActivity.instance.findViewById(R.id.coverImage);


        infoText = (TextView) MainActivity.instance.findViewById(R.id.infoText);

        infoText.setSelected(true);
        infoText.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

            }
        });

        durationText = (TextView) MainActivity.instance
                .findViewById(R.id.durationText);
        currentPositionText = (TextView) MainActivity.instance
                .findViewById(R.id.currentPositionText);


        updatePositionText((String) currentPositionText.getText());


        SeekBar songProgressBar = UIController.instance.songProgressBar;


        songProgressBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {



            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                // TODO Auto-generated method stub

                if (fromUser) {
                    currentPosition = (int) ((float) currentDuration * (float) progress * 0.01);
                    updateShowedPosition();
                    setPositionInService(progress);
                }


            }
        });

        //Restore Playing Song from Service
        Log.e("Service.isRunning", Boolean.toString(MyMediaPlayerService.isRunning));

        if (MyMediaPlayerService.isRunning) {
            getServiceInfo();
            Log.e("Service.isRunning", "GT INFO");
        } else {

            getLastSongInfo();

        }


    }


    public void setPlayingSongInfo(Song song) {


        // TODO if(activeSong!=null)
        Log.e("XXX Activity", "Set  Play Song Info");

        activeSong = song;

        setActivePlaylistByGid(song.getPlaylistGid());

        if (!infoText.getText().toString().equals(activeSong.getDisplayName())) {
            final Animation in = new AlphaAnimation(0.0f, 1.0f);
            in.setDuration(350);
            infoText.setText(activeSong.getDisplayName());
            infoText.startAnimation(in);
        }

        UIController.instance.setSongInfoBarEnabled(true, 1);

    }


    public void onPlayButton() {
        Log.e("PLAYBUTON", Boolean.toString(activeSong != null));
        if (activeSong != null) {
            isPlaying = !isPlaying;
            //Pause/Play song in Service

            if (isPlaying) {
                if (!isStarted) {
                    playSong(activeSong);
                } else {
                    onPlay();
                    playInService();
                }

            } else {
                onPause();
                pauseInService();
            }
        }
    }

    public void onNextButton() {
        Log.e("PLAYNextBUTON", Boolean.toString(activeSong != null));
        if (activeSong != null) {

            isStarted = true;
            isPlaying = true;
            isLoaded = false;

            playUpdateHandler.removeCallbacksAndMessages(null);
            currentPosition = 0;
            setCurrentBufferingPosition(0);
            currentPositionText.setText("");
            updateDurationText("");
            playNextInService();

            onPlay();

        }
    }


    public void onPrevButton() {
        Log.e("PLAYPrevBUTON", Boolean.toString(activeSong != null));
        if (activeSong != null) {

            isStarted = true;
            isPlaying = true;
            isLoaded = false;

            playUpdateHandler.removeCallbacksAndMessages(null);
            currentPosition = 0;
            setCurrentBufferingPosition(0);
            currentPositionText.setText("");
            updateDurationText("");

            playPrevInService();

            onPlay();

        }
    }



    private class LoadingCallback implements CustomCallback {
        @Override
        public void callbackString(String returnString) {
        }
    }


    //Send Play Command to Service
    public void playSong(Song song) {

        //Same song
        if (isStarted && this.activeSong != null && this.activeSong.gid.equals(song.gid)) {
            onPlayButton();
        } else {

            isStarted = true;
            isPlaying = true;
            isLoaded = false;

            playUpdateHandler.removeCallbacksAndMessages(null);
            currentPosition = 0;
            setCurrentBufferingPosition(0);
            currentPositionText.setText("");
            updateDurationText("");

            setPlayingSongInfo(song);
            startSongInService();

            onPlay();
        }

    }

    public void onPlay() {


        MainActivity.instance.listController.refreshList();

        //Set Cover Image
        if (activeSong.getIcon() != null)
            coverImage.setImageDrawable(activeSong.getIcon());


        if (isPlaying) {
            //Change Layout
            int sdk = android.os.Build.VERSION.SDK_INT;
            if (sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                UIController.instance.playButton.setBackgroundDrawable(MainActivity.instance.getResources().getDrawable(R.drawable.pausebutton));
            } else {
                UIController.instance.playButton.setBackground(MainActivity.instance.getResources().getDrawable(R.drawable.pausebutton));
            }
        }

        //Set Position every Second
        if (playUpdateThread == null || !playUpdateThread.isAlive()) {
            playUpdateHandler.removeCallbacksAndMessages(null);
            playUpdateThread = new Thread(new Runnable() {

                @Override
                public void run() {
                    while (isPlaying && !MainActivity.instance.destroyed) {
                        try {
                            playUpdateHandler.post(new Runnable() {

                                @Override
                                public void run() {
                                    if (isLoaded)
                                        updateShowedPosition();

                                }
                            });
                            Thread.sleep(1000);
                        } catch (Exception e) {
                            // TODO: handle exception
                        }
                    }
                }
            });
            playUpdateThread.start();
        }
    }

    public void onPause() {
        if (!isPlaying) {
            //Change Layout
            int sdk = android.os.Build.VERSION.SDK_INT;
            if (sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                UIController.instance.playButton.setBackgroundDrawable(MainActivity.instance.getResources().getDrawable(R.drawable.playbutton));
            } else {
                UIController.instance.playButton.setBackground(MainActivity.instance.getResources().getDrawable(R.drawable.playbutton));
            }
        }
    }


    public void updatePositionText(String text) {
        currentPositionText.setText(text);

        currentPositionText.measure(0, 0);
        int widthPos = currentPositionText.getMeasuredWidth();
        durationText.measure(0, 0);
        int widthDur = durationText.getMeasuredWidth();
        int width = Math.max(widthPos, widthDur);

        ViewGroup.LayoutParams params = currentPositionText.getLayoutParams();
        params.width = width;
        currentPositionText.setLayoutParams(params);

        params = durationText.getLayoutParams();
        params.width = width;
        durationText.setLayoutParams(params);


    }

    public void updateDurationText(String text) {

        durationText.setText(text);

        currentPositionText.measure(0, 0);
        int widthPos = currentPositionText.getMeasuredWidth();
        durationText.measure(0, 0);
        int widthDur = durationText.getMeasuredWidth();
        int width = Math.max(widthPos, widthDur);

//        (int)(10* MainActivity.instance.getResources().getDisplayMetrics().density);

        ViewGroup.LayoutParams params = currentPositionText.getLayoutParams();
        params.width = width;
        currentPositionText.setLayoutParams(params);
        params = durationText.getLayoutParams();
        params.width = width;
        durationText.setLayoutParams(params);
    }


    public void updateShowedPosition() {

        float currentPositionFloat = currentPosition;
        String formattedDate;

        if (currentPositionFloat > 0 || isLoaded) {


            currentPositionFloat = currentPositionFloat / 1000;
            float hours = currentPositionFloat / 3600;
            float minutes = (currentPositionFloat % 3600) / 60;
            float seconds = currentPositionFloat % 60;

            if ((int) hours == 0) {
                formattedDate = String.format("%02d:%02d", (int) minutes,
                        (int) seconds);
            } else {
                formattedDate = String.format("%02d:%02d:%02d", (int) hours,
                        (int) minutes, (int) seconds);
            }
        } else
            formattedDate = "";


        updatePositionText(formattedDate);


        SeekBar songProgressBar = UIController.instance.songProgressBar;

        if (currentDuration > 0)
            songProgressBar.setProgress((int) ((float) this.currentPosition
                    / (float) currentDuration * 100.0f));
        else
            songProgressBar.setProgress(0);
    }


    public void setPositionInService(int positionPercent) {

        Log.e("!!!!SET Position", "###");

        Intent intent = new Intent(MainActivity.instance,
                MyMediaPlayerService.class);

        intent.putExtra(MyMediaPlayerService.SEEKPOSITION, positionPercent);

        MainActivity.instance.startService(intent);
    }


    public void getServiceInfo() {

        Log.e("!!!!GET SERVICE INFO", "###");

        Intent intent = new Intent(MainActivity.instance,
                MyMediaPlayerService.class);
        intent.putExtra(MyMediaPlayerService.GET_INFO, true);
        intent.putExtra(MyMediaPlayerService.MESSENGER, new Messenger(messageHandler));

        MainActivity.instance.startService(intent);
    }

    public void startSongInService() {
        Log.e("!!!!STARTSERVICE", "###");

        Intent intent = new Intent(MainActivity.instance,
                MyMediaPlayerService.class);
        intent.putExtra(MyMediaPlayerService.START_LOADPLAY, true);

        intent.putExtra(MyMediaPlayerService.MESSENGER, new Messenger(messageHandler));

        intent.putExtra(MyMediaPlayerService.SONG, gson.toJson(activeSong));


        intent.putExtra(MyMediaPlayerService.PGID, activeSong.getPlaylistGid());


        MainActivity.instance.startService(intent);


    }

    public void playInService() {
        Log.e("!!!!PLAYSERVICE", "###");
        Intent intent = new Intent(activity, MyMediaPlayerService.class);
        intent.putExtra(MyMediaPlayerService.START_PLAY, true);
        intent.putExtra("MESSENGER", new Messenger(messageHandler));

        activity.startService(intent);


    }

    public void pauseInService() {
        Log.e("!!!!PAUSESERVICE", "###");
        Intent intent = new Intent(activity, MyMediaPlayerService.class);
        intent.putExtra(MyMediaPlayerService.START_PAUSE, true);
        intent.putExtra("MESSENGER", new Messenger(messageHandler));
        activity.startService(intent);
    }


    private void playNextInService() {

        Intent intent = new Intent(activity, MyMediaPlayerService.class);
        intent.putExtra(MyMediaPlayerService.START_PLAYNEXT, true);
        intent.putExtra(MyMediaPlayerService.SONG, gson.toJson(activeSong));
        intent.putExtra("MESSENGER", new Messenger(messageHandler));
        activity.startService(intent);
    }


    private void playPrevInService() {
        Intent intent = new Intent(activity, MyMediaPlayerService.class);
        intent.putExtra(MyMediaPlayerService.START_PLAYPREV, true);
        intent.putExtra(MyMediaPlayerService.SONG, gson.toJson(activeSong));
        intent.putExtra("MESSENGER", new Messenger(messageHandler));

        activity.startService(intent);
    }


    public synchronized void onDurationChanged(int duration) {


        currentDuration = duration;
        if (currentDuration == -1) {
            currentDuration = 0;
            updateDurationText("");
        } else {
            float durationFloat = duration;
            durationFloat = durationFloat / 1000;
            float hours = durationFloat / 3600;
            float minutes = (durationFloat % 3600) / 60;
            float seconds = durationFloat % 60;

            String formattedDate;
            if ((int) hours == 0) {
                formattedDate = String.format("%02d:%02d", (int) minutes,
                        (int) seconds);
            } else {
                formattedDate = String.format("%02d:%02d:%02d", (int) hours,
                        (int) minutes, (int) seconds);
            }
            updateDurationText(formattedDate);
        }
    }

    public void onReset() {
        PlayController.instance.isPlaying = false;
        PlayController.instance.isLoaded = false;
        PlayController.instance.onCurrentBufferingPositionChanged(0);
        PlayController.instance.onCurrentPositionChanged(0);
        PlayController.instance.updateShowedPosition();

        UIController.instance.surfaceViewBlack.setVisibility(View.VISIBLE);
    }

    public void onLoaded() {

        //Song is ready to Play
        //Enable Progressbar
        UIController.instance.setSongProgressBarEnabled(true, 1);
        isLoaded = true;
        final Animation in = new AlphaAnimation(0.0f, 1.0f);
        in.setDuration(700);
        currentPositionText.startAnimation(in);
        durationText.startAnimation(in);
        updateShowedPosition();

        boolean isVideo = false;

        final MediaPlayer actMediaPlayer = MyMediaPlayerService.instance.servicePlayController.mediaPlayer;
        try {
            isVideo = actMediaPlayer.getVideoHeight() > 0;
        } catch (Exception e) {
        }

        if (!isVideo || !UIController.instance.showVideo) {
            View surfaceViewParent = UIController.instance.surfaceViewParent;
            android.view.ViewGroup.LayoutParams lp = surfaceViewParent.getLayoutParams();
            lp.height = 0;
            surfaceViewParent.setLayoutParams(lp);
        } else {


            //Set Video
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {


                    SurfaceHolder actSurfaceHolder;
                    do {
                        synchronized (UIController.instance.surfaceLock) {
                            final SurfaceHolder surfaceHolder;
                            final View surfaceViewParent = UIController.instance.surfaceViewParent;
                            final android.view.ViewGroup.LayoutParams lp = surfaceViewParent.getLayoutParams();
                            final int screenWidth = ((WindowManager) MainActivity.instance.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getWidth();

                            //Get the SurfaceView layout parameters
                            surfaceHolder = UIController.instance.surfaceHolder;
                            actSurfaceHolder = surfaceHolder;


                            //Set Video
                            Handler mainHandler = new Handler(MainActivity.instance.getMainLooper());
                            Runnable runnable = new Runnable() {
                                @Override
                                public void run() {
                                    if (surfaceHolder != null) {
                                        Log.e("SET DISPLAY!!!", "222222222222222222222222222222222222");
                                        try {
                                            actMediaPlayer.setDisplay(surfaceHolder);
                                        } catch (java.lang.IllegalArgumentException e) {
                                            e.printStackTrace();
                                            actMediaPlayer.setDisplay(null);
                                        }
                                        //Get the dimensions of the video
                                        int videoWidth = actMediaPlayer.getVideoWidth();
                                        int videoHeight = actMediaPlayer.getVideoHeight();
                                        if (videoWidth != 0 && videoHeight != 0)
                                            //Get the width of the screen
                                            //Set the height of the SurfaceView to match the aspect ratio of the video
                                            //be sure to cast these as floats otherwise the calculation will likely be 0
                                            lp.height = (int) (((float) videoHeight / (float) videoWidth) * (float) screenWidth);
                                        else
                                            lp.height = 0;
                                        //Commit the layout parameters
                                    } else
                                        lp.height = 1;
                                    //Set the width of the SurfaceView to the width of the screen
                                    lp.width = screenWidth;
                                    surfaceViewParent.setLayoutParams(lp);
                                }
                            };
                            mainHandler.post(runnable);
                            if (surfaceHolder == null) {
                                try {
                                    UIController.instance.surfaceLock.wait();
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    } while (actSurfaceHolder == null);
                    //Make Blackscreen invisible
                    Handler mainHandler = new Handler(MainActivity.instance.getMainLooper());
                    Runnable runnable = new Runnable() {
                        @Override
                        public void run() {
                            UIController.instance.surfaceViewBlack.setVisibility(View.INVISIBLE);
                        }
                    };
                    mainHandler.post(runnable);

                }

            });
            thread.start();
        }


    }


    public void onCurrentPositionChanged(int currentPosition) {

        if (currentPosition <= currentDuration || currentDuration <= 0)
            this.currentPosition = currentPosition;


    }


    public void onCurrentBufferingPositionChanged(int currentPosition) {

        if (currentPosition <= currentDuration || currentDuration <= 0) {
            setCurrentBufferingPosition(currentPosition);
        }

    }

    public void setCurrentBufferingPosition(int currentPosition) {
        SeekBar songProgressBar = UIController.instance.songProgressBar;
        songProgressBar.setSecondaryProgress(currentPosition);
    }


    public void getLastSongInfo() {

        SharedPreferences data = MainActivity.instance.getSharedPreferences(PersistenceController.PERSISTENCEDATA_NAME, 0);

        String activeSongString = data.getString("activeSong", "");

        if (!activeSongString.equals("")) {
            Song lastActiveSong = gson.fromJson(activeSongString, Song.class);

            setPlayingSongInfo(lastActiveSong);

            setActivePlaylistByGid(data.getString("activePlaylistGid", ""));
        }

    }

    public boolean isPlayingSong(Song song) {
        return song.getPlaylistGid().equals(MainActivity.instance.playController.activePlaylistGid)&&song.gid.equals(MainActivity.instance.playController.activeSong.gid);
    }

    public Song getSongFromSongGid(String songGid) {

        if(activePlaylist!=null){
            for (MainListElement  songElement  : activePlaylist.getList()) {
                Song song = ((SongListElement)songElement).getSong();
                if(song.gid.equals(songGid))
                    return song;
            }
        }

        return null;
    }



    public Playlist getPlaylistFromPlaylistGid(String playlistGid) {
        for (MainListElement playlistElement : MyMusicController.instance.list) {
            PlaylistListElement playlistListElement = (PlaylistListElement) playlistElement;
            Playlist playlist = playlistListElement.getPlaylist();
            if (playlist.getGid().equals(playlistGid)) {
                return playlist;
            }
        }
        return null;
    }

    public void setActivePlaylistByGid(String playlistGid) {
        activePlaylist = getPlaylistFromPlaylistGid(playlistGid);
        if (activePlaylist != null) {
            activePlaylistGid = playlistGid;

            OptionListElement currentPlaylistElement = ((MainMode) (UIController.viewModes[UIController.MAINMODE]).mode).currentPlaylistElement;
            currentPlaylistElement.info = activePlaylist.getName();
            currentPlaylistElement.listLayout = ListAdapter.ListLayout.NAMEINFO;

        }
    }


    public Playlist getActivePlaylist() {
        return activePlaylist;
    }


}
