package com.kdi.excore.entities;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.media.MediaPlayer;

import com.kdi.excore.R;
import com.kdi.excore.game.Game;
import com.kdi.excore.xfx.AudioPlayer;

/**
 * Created by Krum Iliev on 5/22/2015.
 */
public class Player {

    private float x;
    private float y;
    private float r;

    private float dx;
    private float dy;
    private int speed;

    private long firingTimer;
    private long firingDelay;

    private boolean recovering;
    private long recoveryTimer;

    private int lives;
    private int score;

    private int powerLevel;
    private int power;
    private int[] requiredPower = {1, 2, 3, 4, 1};

    private Game gameView;

    public Player(Game gameView) {
        this.gameView = gameView;

        x = gameView.getWidth() / 2;
        y = gameView.getHeight() - 50;
        r = 20;

        dx = 0;
        dy = 0;
        speed = 5;

        lives = 3;

        firingTimer = System.nanoTime();
        firingDelay = 200;

        recovering = false;
        recoveryTimer = 0;
        score = 0;
    }

    public void update() {
        move();
        shoot();

        if (recovering) {
            long elapsed = (System.nanoTime() - recoveryTimer) / 1000000;
            if (elapsed > 2000) {
                recovering = false;
                recoveryTimer = 0;
            }
        }
    }

    public void move() {
        x += dx;
        y += dy;

        if (x < r) {
            x = r;
            dx = 0;
            dy = 0;
        }
        if (y < r) {
            y = r;
            dx = 0;
            dy = 0;
        }
        if (x > gameView.getWidth() - r) {
            x = gameView.getWidth() - r;
            dx = 0;
            dy = 0;
        }
        if (y > gameView.getHeight() - r) {
            y = gameView.getHeight() - r;
            dx = 0;
            dy = 0;
        }
    }

    public void shoot() {
        long elapsed = (System.nanoTime() - firingTimer) / 1000000;

        if (elapsed > firingDelay) {
            firingTimer = System.nanoTime();

            if (powerLevel == 0) {
                gameView.playSound(AudioPlayer.SOUND_WEAPON_0);
                gameView.addBullet(270, x, y - 10);
            }

            if (powerLevel == 1) {
                gameView.playSound(AudioPlayer.SOUND_WEAPON_1);
                gameView.addBullet(270, x + 10, y - 10);
                gameView.addBullet(270, x - 10, y - 10);
            }

            if (powerLevel == 2) {
                gameView.playSound(AudioPlayer.SOUND_WEAPON_2);
                gameView.addBullet(275, x + 10, y - 10);
                gameView.addBullet(265, x - 10, y - 10);
            }

            if (powerLevel == 3) {
                gameView.playSound(AudioPlayer.SOUND_WEAPON_3);
                gameView.addBullet(270, x, y - 10);
                gameView.addBullet(270, x + 10, y - 10);
                gameView.addBullet(270, x - 10, y - 10);
            }

            if (powerLevel == 4) {
                gameView.playSound(AudioPlayer.SOUND_WEAPON_4);
                gameView.addBullet(270, x, y - 10);
                gameView.addBullet(275, x + 5, y - 10);
                gameView.addBullet(265, x - 5, y - 10);
            }
        }
    }

    public void draw(Canvas canvas) {
        Paint paint = new Paint();
        paint.setAntiAlias(true);

        if (recovering) {
            paint.setStyle(Paint.Style.FILL);
            paint.setColor(Color.RED);
            canvas.drawCircle(x, y, r, paint);

            paint.setStyle(Paint.Style.STROKE);
            paint.setColor(Color.WHITE);
            paint.setStrokeWidth(4);
            canvas.drawCircle(x, y, r, paint);
        } else {
            paint.setStyle(Paint.Style.FILL);
            paint.setColor(Color.GRAY);
            canvas.drawCircle(x, y, r, paint);

            paint.setStyle(Paint.Style.STROKE);
            paint.setColor(Color.WHITE);
            paint.setStrokeWidth(4);
            canvas.drawCircle(x, y, r, paint);
        }
    }

    public void setDestination(float newX, float newY) {
        double tx = newX - x;
        double ty = newY - y;
        double dir = Math.atan2(ty, tx);

        dx = (float) (speed * Math.cos(dir));
        dy = (float) (speed * Math.sin(dir));
    }

    public void loseLife() {
        lives--;
        recovering = true;
        recoveryTimer = System.nanoTime();
    }

    public void gainLife() {
        lives++;
        if (lives > 5) lives = 5;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public float getR() {
        return r;
    }

    public int getLives() {
        return lives;
    }

    public boolean isRecovering() {
        return recovering;
    }

    public int getScore() {
        return score;
    }

    public void addScore(int i) {
        score += i;
    }

    public void increasePower(int i) {
        power += i;
        if (power >= requiredPower[powerLevel]) {
            power -= requiredPower[powerLevel];
            powerLevel++;
            if (powerLevel >= requiredPower.length) {
                powerLevel = requiredPower.length - 1;
                power = requiredPower[powerLevel];
            }
        }
    }

    public int getPowerLevel() {
        return powerLevel;
    }

    public int getPower() {
        return power;
    }

    public int getRequiredPower() {
        return requiredPower[powerLevel];
    }
}
