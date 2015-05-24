package com.kdi.excore.game;

import android.graphics.Canvas;

/**
 * Created by Krum Iliev on 5/22/2015.
 */
public class GameThread extends Thread {

    private static final String LOG_TAG = GameThread.class.getSimpleName();

    private int FPS = 60;

    private Game mGamePanel; // The actual view that handles inputs and draws to the surface

    private boolean running;

    public GameThread(Game gamePanel) {
        super();
        mGamePanel = gamePanel;
    }

    public void setRunning(boolean running) {
        this.running = running;
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
            try {
                mGamePanel.update();
                c = mGamePanel.getHolder().lockCanvas();
                synchronized (mGamePanel.getHolder()) {
                    mGamePanel.draw(c);
                }
            } finally {
                if (c != null) {
                    mGamePanel.getHolder().unlockCanvasAndPost(c);
                }
            }

            URDTimeMillis = (System.nanoTime() - startTime) / 1000000;
            waitTime = targetTime - URDTimeMillis;
            if (waitTime < 0) waitTime = 5;
            try {
                sleep(waitTime);
            } catch (Exception e) {
            }
        }
    }
}

