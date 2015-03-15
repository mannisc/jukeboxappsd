package com.songbase.fm.androidapp.playing.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.RemoteViews;

import com.google.gson.Gson;
import com.songbase.fm.androidapp.MainActivity;
import com.songbase.fm.androidapp.R;
import com.songbase.fm.androidapp.list.MainListElement;
import com.songbase.fm.androidapp.media.Song;
import com.songbase.fm.androidapp.misc.DownloadImageTask;
import com.songbase.fm.androidapp.mymusic.MyMusicController;
import com.songbase.fm.androidapp.persistence.PersistenceController;

import static com.songbase.fm.androidapp.misc.DownloadImageTask.*;


public class MyMediaPlayerService extends Service {


    public static boolean isRunning = false;


    private static final int classID = 579; // just a number


    public static final Gson gson = new Gson();

    public static final String START_LOADPLAY = "START_LOADPLAY";
    public static final String START_PAUSE = "START_PAUSE";
    public static final String START_PLAY = "START_PLAY";
    public static final String START_PLAYNEXT = "START_PLAYNEXT";
    public static final String START_PLAYPREV = "START_PLAYPREV";
    public static final String START_STOP = "START_STOP";


    public static final String GET_INFO = "GET_INFO";
    public static final String SEEKPOSITION = "SEEKPOSITION";

    public static final String MESSENGER = "MESSENGER";

    public static final String SONG = "SONG";
    public static final String PGID = "PGID";

    private static Messenger messageHandler;
    public static MyMediaPlayerService instance;


    public ServicePlayController servicePlayController;
    private ServiceBufferController serviceBufferController;
    public Activity activity;


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        serviceBufferController = new ServiceBufferController(this);
        servicePlayController = new ServicePlayController(this, serviceBufferController);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e("!!!!", "onStartCommand");

        if (intent == null)
            return Service.START_STICKY;


        //Set Service to running
        isRunning = true;

        if (intent.hasExtra(MESSENGER))
            messageHandler = (Messenger) intent.getExtras().get(MESSENGER);

        Song intentSong = null;
        if (intent.hasExtra(SONG))
            intentSong = gson.fromJson(intent.getStringExtra(SONG), Song.class);

        //Start new Song
        if (intent.getBooleanExtra(START_LOADPLAY, false)) {
            //Load Song
            if (intentSong != null) {
                if (servicePlayController.isPlaying)
                    servicePlayController.reset();

                servicePlayController.getPlaylistSongs(intentSong.getPlaylistGid(), false);
                servicePlayController.startSong(intentSong);

            }


        } else if (intent.getBooleanExtra(START_PLAY, false)) {
            this.play();
        } else if (intent.getBooleanExtra(START_PAUSE, false)) {
            Log.e("!!!!", "intent.getBooleanExtra(START_PAUSE)");
            this.pause();
        } else if (intent.getBooleanExtra(START_PLAYNEXT, false)) {
            this.next(intentSong);
        } else if (intent.getBooleanExtra(START_PLAYPREV, false)) {
            this.prev(intentSong);
        }else if (intent.getBooleanExtra(START_STOP, false)) {
            cancelNotifcation();
            this.pause();
        } else if (intent.hasExtra(SEEKPOSITION)) {
            servicePlayController.setPositionPercent(intent.getIntExtra(SEEKPOSITION, -1));
        } else if (intent.getBooleanExtra(GET_INFO, false)) {
            sendInfo();
        }


        return Service.START_STICKY;
    }


    @Override
    public void onDestroy() {
        servicePlayController.stop();
        isRunning = false;
    }

    public  void cancelNotifcation() {
        NotificationManager notificationManager =
                (NotificationManager)this.getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(classID);
    }

    @SuppressLint("NewApi")
    public void createNotifcation() {

        if (1 == 2) {//TODO Remove

            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                    | Intent.FLAG_ACTIVITY_SINGLE_TOP);

            PendingIntent pi = PendingIntent.getActivity(this, 0, intent, 0);
            Notification.Builder builder = new Notification.Builder(
                    getApplicationContext()).setContentTitle("Songbase.fm")
                    .setContentText("Now Playing: \"Rain\"")
                    .setSmallIcon(R.drawable.ic_launcher).setContentIntent(pi);
            Notification notification;
            if (android.os.Build.VERSION.SDK_INT < 16) {
                notification = builder.getNotification();
            } else {
                notification = builder.build();
            }

            startForeground(classID, notification);
        }


        PendingIntent coverIntent = PendingIntent.getActivity(this, 0, new Intent(this, MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP),
                PendingIntent.FLAG_CANCEL_CURRENT);


        PendingIntent playIntent = getPendingIntent(this,MyMediaPlayerService.START_PAUSE);

        PendingIntent nextIntent = getPendingIntent(this,MyMediaPlayerService.START_PLAYNEXT);

        PendingIntent prevIntent = getPendingIntent(this,MyMediaPlayerService.START_PLAYPREV);

        PendingIntent closeIntent = getPendingIntent(this,MyMediaPlayerService.START_STOP);

        /* Construct the remote view to pass as the notification content. */

        RemoteViews v = new RemoteViews(this.getPackageName(), R.layout.notification_player);

        v.setOnClickPendingIntent(R.id.cover, coverIntent);
        v.setOnClickPendingIntent(R.id.previous, prevIntent);
        v.setOnClickPendingIntent(R.id.play, playIntent);
        v.setOnClickPendingIntent(R.id.next, nextIntent);
        v.setOnClickPendingIntent(R.id.close, closeIntent);

        /*
         * Finally, build and post the notification. Note that we mark it as an
         * ongoing event.
         */

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setContent(v);
        builder.setSmallIcon(R.drawable.ic_launcher);
        // notification.bigContentView = v;

        final Notification notification = builder.build();

        notification.flags |= Notification.FLAG_ONGOING_EVENT;

        Log.e("Notification", "createNotifcation---------------------------------");

        servicePlayController.activeSong.getIconCallback(new DownloadImageTask.DownloadCallback() {
            @Override
            public void callback(Bitmap result) {
                Log.e("RESULT bbbbbb", "ddsdfsdfsdf " + Boolean.toString((result != null)));
                if (result != null)
                    notification.contentView.setImageViewBitmap(R.id.cover, result);
                else
                    notification.contentView.setImageViewResource(R.id.cover, R.drawable.ic_launcher);


                NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(
                        getApplicationContext().NOTIFICATION_SERVICE);


                notificationManager.notify(classID, notification);
            }
        });

        notification.contentView.setImageViewResource(R.id.cover, R.drawable.ic_launcher);

        notification.contentView.setTextViewText(R.id.caption, servicePlayController.activeSong.getDisplayName());

        startForeground(classID, notification);


    }

    private  PendingIntent getPendingIntent(Context context, String extra) {
        Intent intent = new Intent(this, MyMediaPlayerService.class);
        intent.putExtra(extra, true);
        intent.setAction(extra);
        /*
         * Without FLAG_UPDATE_CURRENT, extras are not sent. Additionally,
         * setAction() is required to make filterEquals() return false;
         * otherwise the same extra is delivered on each intent.
         */

        return PendingIntent.getService(context, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
    }

    public void sendInfo() {
        //Song
        Bundle data = new Bundle();
        data.putString("SONG", gson.toJson(servicePlayController.activeSong));

        data.putBoolean("ISPLAYING", servicePlayController.isPlaying);

        sendMessage(ServiceMessageHandler.MESSAGETYPE_INFO, data);


        //Duration
        data = new Bundle();
        int duration = servicePlayController.getDuration();

        data.putInt("DURATIONVALUE", duration);
        sendMessage(ServiceMessageHandler.MESSAGETYPE_DURATION, data);


    }

    public void sendUpdatedPlayedSongs(String songsJSON) {

        if (MainActivity.instance != null && MainActivity.instance.loaded) {
            Bundle data = new Bundle();
            data.putString("SONGS", songsJSON);
            sendMessage(ServiceMessageHandler.MESSAGETYPE_PLAYEDSONGSUPDATE, data);

        }


    }

    public void sendBufferedPosition(int position) {
        Bundle data = new Bundle();

        data.putInt("BUFFEREDVALUE", position);

        sendMessage(ServiceMessageHandler.MESSAGETYPE_BUFFERED, data);
    }


    public void sendPosition(int position) {
        Bundle data = new Bundle();

        data.putInt("POSITIONVALUE", position);
        sendMessage(ServiceMessageHandler.MESSAGETYPE_POSITION, data);
    }


    public void sendDuration(int duration) {
        Bundle data = new Bundle();
        if (duration == -1)
            duration = 0;
        data.putInt("DURATIONVALUE", duration);
        sendMessage(ServiceMessageHandler.MESSAGETYPE_DURATION, data);

    }

    public void sendLoaded() {
        Bundle data = new Bundle();
        sendMessage(ServiceMessageHandler.MESSAGETYPE_LOADED, data);

    }

    public void sendStopped() {
        Bundle data = new Bundle();
        sendMessage(ServiceMessageHandler.MESSAGETYPE_STOPPED, data);
    }

    public void sendReset(int i) {
        Bundle data = new Bundle();
        sendMessage(ServiceMessageHandler.MESSAGETYPE_RESET, data);
    }

    public void sendMessage(String type, Bundle data) {
        if (messageHandler != null) {
            Message message = Message.obtain();

            data.putString(ServiceMessageHandler.MESSAGETYPE, type);

            message.setData(data);

            try {
                messageHandler.send(message);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }


    public void play() {
        servicePlayController.play();
    }

    public void pause() {
        servicePlayController.pause();
    }

    public void prev(Song song) {
        if (servicePlayController.activeSong == null && song != null) {
            servicePlayController.activeSong = song;
            servicePlayController.getPlaylistSongs(song.getPlaylistGid(), false);
        }

        servicePlayController.playPrev();
    }

    public void next(Song song) {
        if (servicePlayController.activeSong == null && song != null) {
            servicePlayController.activeSong = song;
            servicePlayController.getPlaylistSongs(song.getPlaylistGid(), false);
        }
        servicePlayController.playNext();
    }


    private void playMp3NICHTVERWENDET(byte[] mp3SoundByteArray) {

        try {
            // create temp file that will hold byte array
            File tempMp3 = File.createTempFile("kurchina", "mp3",
                    MainActivity.instance.getCacheDir());
            tempMp3.deleteOnExit();
            FileOutputStream fos = new FileOutputStream(tempMp3);
            fos.write(mp3SoundByteArray);
            fos.close();

            // Tried reusing instance of media player
            // but that resulted in system crashes...
            MediaPlayer mediaPlayer = new MediaPlayer();

            // Tried passing path directly, but kept getting
            // "Prepare failed.: status=0x1"
            // so using file descriptor instead
            FileInputStream fis = new FileInputStream(tempMp3);
            mediaPlayer.setDataSource(fis.getFD());

            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (IOException ex) {
            String s = ex.toString();
            ex.printStackTrace();
        }
    }


}