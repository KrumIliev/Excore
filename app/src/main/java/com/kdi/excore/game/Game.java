package com.kdi.excore.game;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.kdi.excore.R;
import com.kdi.excore.states.MenuState;
import com.kdi.excore.states.StateManager;
import com.kdi.excore.utils.Utils;
import com.kdi.excore.xfx.AudioPlayer;

/**
 * Created by Krum Iliev on 5/22/2015.
 */
public class Game extends SurfaceView implements SurfaceHolder.Callback {

    private static final String LOG_TAG = Game.class.getSimpleName();

    public int width, height;

    private GameThread thread;

    public Typeface tf;

    public int background;
    public AudioPlayer audioPlayer;

    public Paint paint;

    private StateManager stateManager;

    public Game(Context context) {
        super(context);
        getHolder().addCallback(this);
        thread = new GameThread(this);
        setFocusable(true);

        background = Utils.getRandomColor(false);

        tf = Typeface.createFromAsset(getContext().getAssets(), "font.ttf");
        audioPlayer = new AudioPlayer(getContext());
        audioPlayer.playMusic(R.raw.track_1);

        paint = new Paint();
        paint.setAntiAlias(true);

        stateManager = new StateManager();
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        width = getWidth();
        height = getHeight();

        stateManager.push(new MenuState(stateManager, this, Utils.getRandomColor(false)));

        thread.setRunning(true);
        thread.start();
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

        thread.setRunning(false);
        while (true) {
            try {
                thread.join();
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
}
