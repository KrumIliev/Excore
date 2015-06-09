package com.kdi.excore.entities;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import com.kdi.excore.game.Game;
import com.kdi.excore.states.PlayState;
import com.kdi.excore.states.State;
import com.kdi.excore.utils.ExcoreSharedPreferences;

import java.util.Random;

/**
 * Created by Krum Iliev on 5/23/2015.
 */
public class Enemy extends Entity {

    public static final int TYPE_NORMAL = 1;
    public static final int TYPE_FAST = 2;
    public static final int TYPE_STRONG = 3;
    public static final int TYPE_IMMUNE = 4;
    public static final int TYPE_BOSS = 5;

    public int health;
    public int type;
    public int rank;

    public boolean ready;
    public boolean dead;

    public boolean hit;
    public long hitTimer;

    public boolean slow;
    public boolean fast;

    public int color;

    private State state;

    public double multiplier;

    public int colorAlpha = 200;

    public Enemy(Game gameView, State state, int type, int rank, double multiplier) {
        this.type = type;
        this.rank = rank;
        this.game = gameView;
        this.state = state;
        this.multiplier = multiplier;

        if (x == 0) {
            x = Math.random() * gameView.width / 2 + gameView.width / 4;
            y = -r;
        }

        setBaseStats();
        setRank();
        if (multiplier != 1) applyMultiplier();

        double angle = Math.random() * 140 + 20;
        rad = Math.toRadians(angle);

        dx = Math.cos(rad) * speed;
        dy = Math.sin(rad) * speed;

        ready = false;
        dead = false;
        hit = false;
        hitTimer = 0;
    }

    public Enemy(Game gameView, State state, int type, int rank, double x, double y, double multiplier, int color) {
        this(gameView, state, type, rank, multiplier);

        this.x = x;
        this.y = y;
        this.color = color;
    }

    public Enemy(Game gameView, State state, int type, int rank, double x, double y, double multiplier) {
        this(gameView, state, type, rank, multiplier);

        this.x = x;
        this.y = y;
    }

    @Override
    public boolean update() {
        if (slow && type != TYPE_IMMUNE) {
            x += dx * 0.3;
            y += dy * 0.3;
        } else if (fast && type != TYPE_IMMUNE) {
            x += dx * 1.5;
            y += dy * 1.5;
        } else {
            x += dx;
            y += dy;
        }

        if (!ready) {
            if (x > r && x < game.width - r && y > r && y < game.height - r)
                ready = true;
        }

        if (x < r && dx < 0) dx = -dx;
        if (y < r && dy < 0) dy = -dy;
        if (x > game.width - r && dx > 0) dx = -dx;
        if (y > game.height - r && dy > 0) dy = -dy;

        if (hit) {
            long elapsed = (System.nanoTime() - hitTimer) / 1000000;
            if (elapsed > 50) {
                hit = false;
                hitTimer = 0;
            }
        }

        return false;
    }

    @Override
    public void draw(Canvas canvas) {
        if (hit) {
            game.paint.setStyle(Paint.Style.FILL);
            game.paint.setColor(Color.WHITE);
            canvas.drawCircle((float) x, (float) y, (float) r, game.paint);
        } else {
            game.paint.setStyle(Paint.Style.FILL);
            game.paint.setColor(color);
            canvas.drawCircle((float) x, (float) y, (float) r, game.paint);

            game.paint.setStyle(Paint.Style.STROKE);
            game.paint.setColor(Color.WHITE);
            game.paint.setStrokeWidth(4);
            canvas.drawCircle((float) x, (float) y, (float) r, game.paint);
        }

        game.resetPaint();
    }

    public void hit() {
        health--;
        if (health <= 0) dead = true;
        hit = true;
        hitTimer = System.nanoTime();
    }

    public void destroy() {
        if (type != TYPE_IMMUNE && type != TYPE_BOSS) dead = true;
    }

    public void explode() {
        if (rank > 1) {
            int amount = 3;
            if (type == TYPE_BOSS) amount = 2;

            for (int i = 0; i < amount; i++) {
                Enemy enemy = new Enemy(game, state, type, rank - 1, this.x, this.y, this.multiplier, this.color);
                double angle;
                if (!ready)
                    angle = Math.random() * 140 + 20;
                else
                    angle = Math.random() * 360;
                enemy.rad = Math.toRadians(angle);
                ((PlayState) state).addEnemy(enemy);
            }
        }
    }

    private void setBaseStats() {
        boolean low = game.preferences.getSetting(ExcoreSharedPreferences.KEY_TRANS);

        if (type == TYPE_NORMAL) {
            color = low ? Color.BLUE : Color.argb(colorAlpha, 0, 0, 255);
            speed = 2;
            r = 15;
            health = 1;
        }

        if (type == TYPE_FAST) {
            color = low ? Color.GREEN : Color.argb(colorAlpha, 0, 255, 0);
            speed = 3;
            r = 20;
            health = 2;
        }

        if (type == TYPE_STRONG) {
            color = low ? Color.MAGENTA : Color.argb(colorAlpha, 255, 0, 255);
            speed = 1.5;
            r = 30;
            health = 5;
        }

        if (type == TYPE_IMMUNE) {
            color = low ? Color.YELLOW : Color.argb(colorAlpha, 255, 255, 0);
            speed = 2;
            r = 20;
            health = 2;
        }

        if (type == TYPE_BOSS) {
            Random rnd = new Random();
            color = low ? Color.rgb(rnd.nextInt(255), rnd.nextInt(255), rnd.nextInt(255)) : Color.argb(colorAlpha, rnd.nextInt(255), rnd.nextInt(255), rnd.nextInt(255));
            speed = 2;
            Random random = new Random();
            r = random.nextInt(30 - 15) + 15;
            health = 10;
        }
    }

    private void setRank() {
        if (rank != 1) {
            for (int i = 1; i < rank; i++) {
                r = r * 1.5;
            }
            health = health + rank;
        }
    }

    private void applyMultiplier() {
        speed += (multiplier / 2);
        health *= multiplier;
    }
}
