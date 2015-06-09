package com.kdi.excore.game;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Typeface;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;

import com.kdi.excore.states.StateManager;
import com.kdi.excore.states.menu.MainMenuState;
import com.kdi.excore.utils.Utils;
import com.kdi.excore.utils.ExcoreSharedPreferences;
import com.kdi.excore.xfx.AudioPlayer;

/**
 * Created by Krum Iliev on 5/22/2015.
 */
public class Game extends SurfaceView implements SurfaceHolder.Callback, Runnable, Thread.UncaughtExceptionHandler {

    public static final String LOG_TAG = Game.class.getSimpleName();

    public int width, height;
    public float devWidth, devHeight;

    //Used to ensure appropriate threading
    static final Integer monitor = 1;
    //Game thread
    private Thread gameThread;
    //The surface this thread (and only this thread) writes upon
    private SurfaceHolder mSurfaceHolder;

    private int FPS = 60;
    public boolean running, paused;

    public Typeface tf;

    public int background;
    public AudioPlayer audioPlayer;

    public Paint paint;

    private StateManager stateManager;
    public ExcoreSharedPreferences preferences;
    public GameListener litener;

    public Game(Context context, GameListener listener) {
        super(context);
        this.litener = listener;
        init();
    }

    public void init() {
        getHolder().addCallback(this);
        setFocusable(true);

        WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        devWidth = size.x;
        devHeight = size.y;

        Log.d(LOG_TAG, "Screen w: " + devWidth + " Screen h: " + devHeight);

        height = 800;

        if (devHeight > 900) {
            width = (int) (devWidth / (devHeight / height));
        } else if (devHeight > 800 && devHeight < 900) {
            width = (int) devWidth;
            height = (int) devHeight;
        } else {
            width = 480;
        }

        Log.d(LOG_TAG, "Canvas w: " + width + " Canvas h: " + height);

        preferences = new ExcoreSharedPreferences(getContext());
        background = Utils.getRandomColor(false);

        tf = Typeface.createFromAsset(getContext().getAssets(), "font.ttf");
        audioPlayer = new AudioPlayer(getContext());
        audioPlayer.playMusic();

        paint = new Paint();
        paint.setAntiAlias(true);

        stateManager = new StateManager();
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        holder.setFixedSize(width, height);
        mSurfaceHolder = holder;

        stateManager.push(new MainMenuState(stateManager, this, Utils.getRandomColor(false)));
        preferences.setSetting(ExcoreSharedPreferences.KEY_MOVE, false);

        running = true;
        gameThread = new Thread(this);
        gameThread.setUncaughtExceptionHandler(this);
        gameThread.start();
        gameThread.setPriority(Thread.MAX_PRIORITY);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        dispose();
    }

    public void dispose() {
        audioPlayer.dispose();

        running = false;
        while (true) {
            try {
                gameThread.join();
                return;
            } catch (InterruptedException e) {
                // try again shutting down the thread
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = Utils.calculatePointScale(width, devWidth, event.getX());
        float y = Utils.calculatePointScale(height, devHeight, event.getY());
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                stateManager.handleInput(x, y);
                break;
            case MotionEvent.ACTION_MOVE:
                if (preferences.getSetting(ExcoreSharedPreferences.KEY_MOVE)) {
                    stateManager.handleInput(x, y);
                }
                break;
        }
        return true;
    }

    public void update() {
        stateManager.update();
    }

    @Override
    public void draw(Canvas canvas) {
        if (canvas == null) return;
        stateManager.draw(canvas);
    }

    public void resetPaint() {
        paint.reset();
        paint.setAntiAlias(true);
    }

    @Override
    public void run() {
        long startTime;
        long URDTimeMillis;
        long waitTime;
        long targetTime = 1000 / FPS;

        Canvas runCanvas;
        while (running) {
            runCanvas = null;
            startTime = System.nanoTime();

            if (!paused) {
                try {
                    runCanvas = mSurfaceHolder.lockCanvas(null);
                    synchronized (monitor) {
                        update();
                        draw(runCanvas);
                    }
                } finally {
                    if (runCanvas != null) {
                        if (mSurfaceHolder != null) mSurfaceHolder.unlockCanvasAndPost(runCanvas);
                    }
                }
            }

            URDTimeMillis = (System.nanoTime() - startTime) / 1000000;
            waitTime = targetTime - URDTimeMillis;
            if (waitTime < 0) waitTime = 5;
            try {
                Thread.sleep(waitTime);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        Log.e(LOG_TAG, ex.toString());
    }
}
