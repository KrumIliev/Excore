package com.kdi.excore.entities;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import com.kdi.excore.R;
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
    public boolean dead;

    public int powerLevel;
    public int power;
    public int[] requiredPower = {1, 2, 3, 3, 2, 0};

    private Game game;
    private PlayState playState;

    private long dischargeTimer;
    private long dischargeDiff;

    public int enemiesKilled = 0;

    public Player(Game gameView, State state) {
        this.game = gameView;
        this.playState = (PlayState) state;

        x = gameView.width / 2;
        y = gameView.height - 50;
        r = 20;

        dx = 0;
        dy = 0;
        speed = 5;

        lives = 3;
        dead = false;

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

        if (dischargeTimer != 0) {
            dischargeDiff = (System.nanoTime() - dischargeTimer) / 1000000;
            if (dischargeDiff > 4000) {
                dischargeTimer = 0;
                power = 0;
                powerLevel--;
            }
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
        if (x > game.width - r) {
            x = game.width - r;
            dx = -dx;
        }
        if (y > game.height - r) {
            y = game.height - r;
            dy = -dy;
        }
    }

    public void shoot() {
        long elapsed = (System.nanoTime() - firingTimer) / 1000000;

        if (elapsed > firingDelay) {
            firingTimer = System.nanoTime();

            if (powerLevel == 0) {
                game.audioPlayer.playSound(AudioPlayer.SOUND_WEAPON_0);
                playState.addBullet(270, x, y - 10);
            }

            if (powerLevel == 1) {
                game.audioPlayer.playSound(AudioPlayer.SOUND_WEAPON_1);
                playState.addBullet(270, x + 10, y - 10);
                playState.addBullet(270, x - 10, y - 10);
            }

            if (powerLevel == 2) {
                game.audioPlayer.playSound(AudioPlayer.SOUND_WEAPON_2);
                playState.addBullet(275, x + 10, y - 10);
                playState.addBullet(265, x - 10, y - 10);
            }

            if (powerLevel == 3) {
                game.audioPlayer.playSound(AudioPlayer.SOUND_WEAPON_3);
                playState.addBullet(270, x, y - 10);
                playState.addBullet(270, x + 10, y - 10);
                playState.addBullet(270, x - 10, y - 10);
            }

            if (powerLevel == 4) {
                game.audioPlayer.playSound(AudioPlayer.SOUND_WEAPON_4);
                playState.addBullet(270, x, y - 10);
                playState.addBullet(275, x + 5, y - 10);
                playState.addBullet(265, x - 5, y - 10);
            }

            if (powerLevel == 5) {
                if (dischargeTimer != 0) {
                    playState.achievements.overcharge = true;
                    game.audioPlayer.playSound(AudioPlayer.SOUND_WEAPON_DISCHARGE);
                    playState.addBullet(270, x, y - 10);
                    playState.addBullet(275, x + 5, y - 10);
                    playState.addBullet(280, x + 5, y - 10);
                    playState.addBullet(285, x + 5, y - 10);
                    playState.addBullet(265, x - 5, y - 10);
                    playState.addBullet(260, x - 5, y - 10);
                    playState.addBullet(255, x - 5, y - 10);
                }
            }
        }
    }

    public void draw(Canvas canvas) {

        game.paint.setStyle(Paint.Style.FILL);
        if (recovering) {
            game.paint.setColor(Color.RED);
        } else if (immortal) {
            game.paint.setColor(Color.YELLOW);
        } else {
            game.paint.setColor(Color.GRAY);
        }
        canvas.drawCircle(x, y, r, game.paint);

        game.paint.setStyle(Paint.Style.STROKE);
        game.paint.setColor(Color.WHITE);
        game.paint.setStrokeWidth(4);
        canvas.drawCircle(x, y, r, game.paint);

        game.resetPaint();
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
        if (lives <= 0) {
            lives = 0;
            dead = true;
        }
        recovering = true;
        recoveryTimer = System.nanoTime();
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

        if (powerLevel == 5) dischargeTimer = System.nanoTime();
    }

    public int getRequiredPower() {
        return requiredPower[powerLevel];
    }

    public void continueGame() {
        score /= 2;
        visibleScore = score;
        lives = 3;
        dead = false;
        recovering = true;
        recoveryTimer = System.nanoTime();
    }
}
