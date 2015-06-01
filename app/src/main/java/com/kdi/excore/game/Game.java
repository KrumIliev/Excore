package com.kdi.excore.game;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.kdi.excore.states.StateManager;
import com.kdi.excore.states.menu.MainMenuState;
import com.kdi.excore.utils.ColorUtils;
import com.kdi.excore.utils.ExcoreSharedPreferences;
import com.kdi.excore.xfx.AudioPlayer;

/**
 * Created by Krum Iliev on 5/22/2015.
 */
public class Game extends SurfaceView implements SurfaceHolder.Callback, Runnable {

    public int width, height;
    private Thread gameThread;

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

        preferences = new ExcoreSharedPreferences(getContext());
        background = ColorUtils.getRandomColor(false);

        tf = Typeface.createFromAsset(getContext().getAssets(), "font.ttf");
        audioPlayer = new AudioPlayer(getContext());
        audioPlayer.playMusic();

        paint = new Paint();
        paint.setAntiAlias(true);

        stateManager = new StateManager();
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        width = getWidth();
        height = getHeight();

        stateManager.push(new MainMenuState(stateManager, this, ColorUtils.getRandomColor(false)));

        running = true;
        gameThread = new Thread(this);
        gameThread.start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {}

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) { dispose(); }

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
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            stateManager.handleInput(event.getX(), event.getY());
        }
        return super.onTouchEvent(event);
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

        while (running) {
            Canvas c = null;
            startTime = System.nanoTime();
            SurfaceHolder holder = getHolder();

            if (!paused) {
                try {
                    update();
                    c = holder.lockCanvas();
                    synchronized (holder) {
                        draw(c);
                    }
                } finally {
                    if (c != null) {
                        holder.unlockCanvasAndPost(c);
                    }
                }
            }

            URDTimeMillis = (System.nanoTime() - startTime) / 1000000;
            waitTime = targetTime - URDTimeMillis;
            if (waitTime < 0) waitTime = 5;
            try {
                Thread.sleep(waitTime);
            } catch (Exception e) {
            }
        }
    }
}
