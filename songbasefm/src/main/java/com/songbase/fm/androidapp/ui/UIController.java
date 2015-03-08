package com.songbase.fm.androidapp.ui;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Handler;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;

import com.songbase.fm.androidapp.MainActivity;
import com.songbase.fm.androidapp.R;
import com.songbase.fm.androidapp.playing.service.MyMediaPlayerService;
import com.songbase.fm.androidapp.ui.navigationbar.NavigationBar;
import com.songbase.fm.androidapp.ui.viewmode.ExploreMode;
import com.songbase.fm.androidapp.ui.viewmode.GenresMode;
import com.songbase.fm.androidapp.ui.viewmode.MainMode;
import com.songbase.fm.androidapp.ui.viewmode.MyMusicMode;
import com.songbase.fm.androidapp.ui.viewmode.SearchMode;
import com.songbase.fm.androidapp.ui.viewmode.ViewMode;

public class UIController implements android.view.SurfaceHolder.Callback {

    // Main Activity
    private Activity activity;

    // Control Buttons
    public ImageButton playButton = null;

    // Progress bar for song
    public SeekBar songProgressBar;
    public boolean songProgressBarEnabled = true;//Will be disabled at startup

    private ListView listView;
    public NavigationBar navigationBar;

    private View progressInfoBar;
    public boolean songInfoBarEnabled = true;//Will be disabled at startup


    public static ViewMode[] viewModes;

    public static ViewMode viewMode;

    public static int MAINMODE = 0;
    public static int SEARCHMODE = 1;
    public static int EXPLOREMODE = 2;
    public static int GENRESMODE = 3;
    public static int MYMUSICMODE = 4;


    private boolean softkeyboardVisible;
    public int orientation;

    public View surfaceViewParent;
    public View surfaceViewBlack;
    public SurfaceView surfaceView;
    public SurfaceHolder surfaceHolder;
    public final Object surfaceLock = new Object();
    public boolean showVideo = false;


    @SuppressLint("NewApi")
    public UIController(Activity activity) {

        softkeyboardVisible = false;

        this.activity = activity;

        viewModes = new ViewMode[5];
        viewModes[MAINMODE] = new MainMode().mode;
        viewModes[SEARCHMODE] = new SearchMode().mode;
        viewModes[EXPLOREMODE] = new ExploreMode().mode;
        viewModes[GENRESMODE] = new GenresMode().mode;
        viewModes[MYMUSICMODE] = new MyMusicMode().mode;

        for (ViewMode viewMode : viewModes) {
            viewMode.init();
        }

        // Mode that defines the UI State
        viewMode = viewModes[0];
        viewMode.activate();


        // Black title bar
        if (Build.VERSION.SDK_INT >= 11) {
            ActionBar bar = activity.getActionBar(); // for color
            if (bar != null)
                bar.setBackgroundDrawable(new ColorDrawable(Color
                        .parseColor("#111111")));
        }


        //Surface View
        surfaceView = (SurfaceView) this.activity.findViewById(R.id.surfaceView);

        surfaceHolder = surfaceView.getHolder();
        surfaceHolder.addCallback(this);
        surfaceHolder = null;

        surfaceViewParent = (View) this.activity.findViewById(R.id.surfaceViewParent);

        surfaceViewBlack = (View) this.activity.findViewById(R.id.surfaceViewBlack);


        // List View
        listView = (ListView) this.activity.findViewById(R.id.listView);
        listView.setBackgroundColor(Color.WHITE);

        // Play Button
        playButton = (ImageButton) this.activity.findViewById(R.id.playButton);
        playButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                MainActivity.instance.playController.onPlayButton();
            }
        });

        // Next Button
        ImageButton playNextButton = (ImageButton) this.activity.findViewById(R.id.nextButton);
        playNextButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                MainActivity.instance.playController.onNextButton();
            }
        });

        // Prev Button
        ImageButton playPrevButton = (ImageButton) this.activity.findViewById(R.id.prevButton);
        playPrevButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                MainActivity.instance.playController.onPrevButton();
            }
        });


        // Progress Info Bar
        progressInfoBar = (View) activity.findViewById(R.id.progressInfoBar);
        setSongInfoBarEnabled(false, 0);

        // Progress Bar
        songProgressBar = (SeekBar) activity.findViewById(R.id.progressBarSong);
        songProgressBar.setProgress(0);
        setSongProgressBarEnabled(false, 0);


        // Navigation Bar
        navigationBar = new NavigationBar();


        //Handle layout change due to keyboard visibility changes
        setKeyboardListener(new OnKeyboardVisibilityListener() {
            @Override
            public void onVisibilityChanged(boolean visible) {
                softkeyboardVisible = visible;

                View controls = (View) MainActivity.instance
                        .findViewById(R.id.controls);
                View songProgressBar = (View) MainActivity.instance.uiController.songProgressBar;

                //Keyboard is visible, hide Controls
                if (softkeyboardVisible) {

                    controls.setVisibility(View.GONE);

                    if (MainActivity.instance.playController.isLoaded)
                        setSongProgressBarEnabled(false, 0);

                    //Keyboard is not visible, show Controls
                } else {

                    new android.os.Handler().postDelayed(new Runnable() {
                        public void run() {
                            if (!softkeyboardVisible) {

                                View controls = (View) MainActivity.instance
                                        .findViewById(R.id.controls);
                                controls.setVisibility(View.VISIBLE);

                                setSongProgressBarEnabled(true, 0);

                            }

                        }
                    }, 50);
                }

            }

        });

        onConfigurationChanged();

    }


    public boolean isModeActive(int mode) {
        return MainActivity.instance.uiController.viewMode == MainActivity.instance.uiController.viewModes[mode];
    }


    /**
     * typeInfoValue:
     * 0: Menü points
     * 0: Playlists
     * 1: Songs
     * @return typeInfoValue

    public int getDisplayedTypeInfo(){

    if( viewMode == viewModes[4]){


    if(viewModes[4].isSubModeActive())
    return 2;
    else
    return 2;


    }
    return 0;
    }
     */


    /**
     * Expand view height
     *
     * @param v
     * @param width
     * @param height
     */
    public static void expandViewHeight(final View v, int width, final int height, float durationMultiplier) {
        final int targetHeight = height;
        v.getLayoutParams().height = 0;
        v.setVisibility(View.VISIBLE);
        Animation a = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                v.getLayoutParams().height = interpolatedTime == 1
                        ? height
                        : (int) (targetHeight * interpolatedTime);
                v.requestLayout();
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        // 1dp/ms
        a.setDuration((int) durationMultiplier * 15 * (int) (targetHeight / v.getContext().getResources().getDisplayMetrics().density));
        v.startAnimation(a);
    }


    /**
     * Enable Song Progress Bar
     *
     * @param enabled
     */
    public void setSongInfoBarEnabled(final boolean enabled, final float durationMultiplier) {
        if (songInfoBarEnabled != enabled) {
            songInfoBarEnabled = enabled;
            // Get a handler that can be used to post to the main thread
            Handler mainHandler = new Handler(activity.getMainLooper());
            Runnable runnable = new Runnable() {

                @Override
                public void run() {
                    progressInfoBar.setEnabled(enabled);
                    RelativeLayout.LayoutParams paramsSeekBar = (RelativeLayout.LayoutParams) progressInfoBar.getLayoutParams();
                    if (enabled) {
                        float scale = activity.getResources().getDisplayMetrics().density;
                        expandViewHeight(progressInfoBar, RelativeLayout.LayoutParams.MATCH_PARENT, (int) (34 * scale + 0.5f), durationMultiplier);
                    } else {
                        paramsSeekBar.height = 0;
                        progressInfoBar.setLayoutParams(paramsSeekBar);
                    }
                }
            }; // This is your code
            mainHandler.post(runnable);
        }
    }

    /**
     * Enable Song Progress Bar
     *
     * @param enabled
     */
    public void setSongProgressBarEnabled(final boolean enabled, final float durationMultiplier) {
        if (songProgressBarEnabled != enabled) {
            songProgressBarEnabled = enabled;
            // Get a handler that can be used to post to the main thread
            Handler mainHandler = new Handler(activity.getMainLooper());
            Runnable runnable = new Runnable() {

                @Override
                public void run() {
                    songProgressBar.setEnabled(enabled);
                    RelativeLayout.LayoutParams paramsSeekBar = (RelativeLayout.LayoutParams) songProgressBar.getLayoutParams();
                    if (enabled) {
                        float scale = activity.getResources().getDisplayMetrics().density;
                        expandViewHeight(songProgressBar, RelativeLayout.LayoutParams.MATCH_PARENT, (int) (20 * scale + 0.5f), durationMultiplier);
                    } else {
                        paramsSeekBar.height = 0;
                        songProgressBar.setLayoutParams(paramsSeekBar);
                    }
                }
            }; // This is your code
            mainHandler.post(runnable);
        }
    }


    // Rotation
    public void onConfigurationChanged() {

        View emptyLeft = (View) this.activity.findViewById(R.id.emptyleft);
        View emptyRight = (View) this.activity.findViewById(R.id.emptyright);

        orientation = this.activity.getResources().getConfiguration().orientation;

        //Center Playcontrolls
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            emptyLeft.setVisibility(View.VISIBLE);
            emptyRight.setVisibility(View.VISIBLE);
        } else {
            emptyLeft.setVisibility(View.GONE);
            emptyRight.setVisibility(View.GONE);
        }

    }

    public void setMode(int mode) {
        viewMode.deactivate();
        viewMode = viewModes[mode];
        viewMode.activate();

    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        this.surfaceHolder = surfaceHolder;

        if (showVideo) {
            final MediaPlayer actMediaPlayer = MyMediaPlayerService.instance.servicePlayController.mediaPlayer;
            if (MainActivity.instance.playController.isLoaded && actMediaPlayer != null) {
                Log.e("SET DISPLAY!!!", "!!!!!!!!!!!!!!!!!!!!!");
                try {
                    actMediaPlayer.setDisplay(surfaceHolder);
                } catch (java.lang.IllegalArgumentException e) {
                    e.printStackTrace();
                    actMediaPlayer.setDisplay(null);
                }
            }

            synchronized (MainActivity.instance.uiController.surfaceLock) {
                surfaceLock.notify();
            }

        }

    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i2, int i3) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        this.surfaceHolder = null;
        MediaPlayer actMediaPlayer = MyMediaPlayerService.instance.servicePlayController.mediaPlayer;
        if (actMediaPlayer != null) {
            actMediaPlayer.setDisplay(null);
        }
    }

    //Keyboard shown/hidden

    public interface OnKeyboardVisibilityListener {
        void onVisibilityChanged(boolean visible);
    }

    public final void setKeyboardListener(
            final OnKeyboardVisibilityListener listener) {
        final View activityRootView = ((ViewGroup) MainActivity.instance
                .findViewById(android.R.id.content)).getChildAt(0);
        activityRootView.getViewTreeObserver().addOnGlobalLayoutListener(
                new OnGlobalLayoutListener() {

                    private boolean wasOpened;
                    private final Rect r = new Rect();

                    @Override
                    public void onGlobalLayout() {
                        activityRootView.getWindowVisibleDisplayFrame(r);

                        int heightDiff = activityRootView.getRootView()
                                .getHeight() - (r.bottom - r.top);
                        boolean isOpen = heightDiff > 100;
                        if (isOpen == wasOpened) {
                            // Ignoring global layout change...
                            return;
                        }

                        wasOpened = isOpen;
                        listener.onVisibilityChanged(isOpen);
                    }
                });
    }

    public void showKeyboard() {
        InputMethodManager imm = (InputMethodManager) MainActivity.instance
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
    }

    public void hideKeyboard() {
        InputMethodManager inputManager = (InputMethodManager) MainActivity.instance
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        // check if no view has focus:
        View view = MainActivity.instance.getCurrentFocus();
        if (view != null) {
            inputManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

}
