package com.kdi.excore.entities;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import com.kdi.excore.game.Game;
import com.kdi.excore.states.PlayState;
import com.kdi.excore.states.State;
import com.kdi.excore.xfx.AudioPlayer;

/**
 * Created by Krum Iliev on 5/22/2015.
 */
public class Player {

    public float x;
    public float y;
    public float r;

    public float dx;
    public float dy;
    public int speed;

    private float nx;
    private float ny;

    public long firingTimer;
    public long firingDelay;

    public boolean recovering;
    public long recoveryTimer;

    public boolean immortal;
    public boolean fly;

    public int lives;
    public int score;
    public int visibleScore;

    public int powerLevel;
    public int power;
    public int[] requiredPower = {1, 2, 3, 4, 2, 0};

    private Game gameView;
    private PlayState playState;

    public Player(Game gameView, State state) {
        this.gameView = gameView;
        this.playState = (PlayState) state;

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

        immortal = false;
        fly = false;

        score = 0;
        visibleScore = 0;
    }

    public void update() {
        move();
        shoot();

        if (score < visibleScore) {
            if (visibleScore - score > 100) score += 2;
            else score++;
        }

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

        if (!fly) {
            if (dy < 0) {
                if (y < ny) dy = 0;
            } else {
                if (y > ny) dy = 0;
            }

            if (dx < 0) {
                if (x < nx) dx = 0;
            } else {
                if (x > nx) dx = 0;
            }
        }

        if (x < r) {
            x = r;
            dx = -dx;
        }
        if (y < r) {
            y = r;
            dy = -dy;
        }
        if (x > gameView.getWidth() - r) {
            x = gameView.getWidth() - r;
            dx = -dx;
        }
        if (y > gameView.getHeight() - r) {
            y = gameView.getHeight() - r;
            dy = -dy;
        }
    }

    public void shoot() {
        long elapsed = (System.nanoTime() - firingTimer) / 1000000;

        if (elapsed > firingDelay) {
            firingTimer = System.nanoTime();

            if (powerLevel == 0) {
                gameView.audioPlayer.playSound(AudioPlayer.SOUND_WEAPON_0);
                playState.addBullet(270, x, y - 10);
            }

            if (powerLevel == 1) {
                gameView.audioPlayer.playSound(AudioPlayer.SOUND_WEAPON_1);
                playState.addBullet(270, x + 10, y - 10);
                playState.addBullet(270, x - 10, y - 10);
            }

            if (powerLevel == 2) {
                gameView.audioPlayer.playSound(AudioPlayer.SOUND_WEAPON_2);
                playState.addBullet(275, x + 10, y - 10);
                playState.addBullet(265, x - 10, y - 10);
            }

            if (powerLevel == 3) {
                gameView.audioPlayer.playSound(AudioPlayer.SOUND_WEAPON_3);
                playState.addBullet(270, x, y - 10);
                playState.addBullet(270, x + 10, y - 10);
                playState.addBullet(270, x - 10, y - 10);
            }

            if (powerLevel == 4) {
                gameView.audioPlayer.playSound(AudioPlayer.SOUND_WEAPON_4);
                playState.addBullet(270, x, y - 10);
                playState.addBullet(275, x + 5, y - 10);
                playState.addBullet(265, x - 5, y - 10);
            }

            if (powerLevel == 5) {
                // TODO add sound
                playState.addBullet(270, x, y - 10);
                playState.addBullet(275, x + 5, y - 10);
                playState.addBullet(280, x + 5, y - 10);
                playState.addBullet(285, x + 5, y - 10);
                playState.addBullet(265, x - 5, y - 10);
                playState.addBullet(260, x - 5, y - 10);
                playState.addBullet(255, x - 5, y - 10);
                power = 0;
                powerLevel--;
            }
        }
    }

    public void draw(Canvas canvas) {

        gameView.paint.setStyle(Paint.Style.FILL);
        if (recovering) {
            gameView.paint.setColor(Color.RED);
        } else if (immortal) {
            gameView.paint.setColor(Color.YELLOW);
        } else {
            gameView.paint.setColor(Color.GRAY);
        }
        canvas.drawCircle(x, y, r, gameView.paint);

        gameView.paint.setStyle(Paint.Style.STROKE);
        gameView.paint.setColor(Color.WHITE);
        gameView.paint.setStrokeWidth(4);
        canvas.drawCircle(x, y, r, gameView.paint);

        gameView.resetPaint();
    }

    public void setDestination(float newX, float newY) {
        double tx = newX - x;
        double ty = newY - y;
        double dir = Math.atan2(ty, tx);

        dx = (float) (speed * Math.cos(dir));
        dy = (float) (speed * Math.sin(dir));

        nx = newX;
        ny = newY;
    }

    public void loseLife() {
        lives--;
        if (lives < 0) lives = 0; //TODO end game
        recovering = true;
        recoveryTimer = System.nanoTime();
        //TODO set power level for more difficulty
        //power = 0;
    }

    public void gainLife() {
        lives++;
        if (lives > 5) lives = 5;
    }

    public void addScore(int i) {
        visibleScore += i;
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

    public int getRequiredPower() {
        return requiredPower[powerLevel];
    }
}
