package com.kdi.excore.entities;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import com.kdi.excore.game.Game;
import com.kdi.excore.states.PlayState;
import com.kdi.excore.states.State;

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

    public int maxRank = 4;
    private State state;

    public Enemy(Game gameView, State state, int type, int rank) {
        this.type = type;
        this.rank = rank;
        this.gameView = gameView;
        this.state = state;

        if (x == 0) {
            x = Math.random() * gameView.width / 2 + gameView.width / 4;
            y = -r;
        }

        setBaseStats();
        setRank();

        double angle = Math.random() * 140 + 20;
        rad = Math.toRadians(angle);

        dx = Math.cos(rad) * speed;
        dy = Math.sin(rad) * speed;

        ready = false;
        dead = false;
        hit = false;
        hitTimer = 0;
    }

    public Enemy(Game gameView, State state, int type, int rank, double x, double y) {
        this(gameView, state, type, rank);

        this.x = x;
        this.y = y;
    }

    @Override
    public boolean update() {
        if (slow) {
            if (type != TYPE_IMMUNE) {
                x += dx * 0.3;
                y += dy * 0.3;
            }
        } else if (fast) {
            if (type != TYPE_IMMUNE) {
                x += dx * 1.5;
                y += dy * 1.5;
            }
        } else {
            x += dx;
            y += dy;
        }

        if (!ready) {
            if (x > r && x < gameView.width - r && y > r && y < gameView.height - r)
                ready = true;
        }

        if (x < r && dx < 0) dx = -dx;
        if (y < r && dy < 0) dy = -dy;
        if (x > gameView.getWidth() - r && dx > 0) dx = -dx;
        if (y > gameView.getHeight() - r && dy > 0) dy = -dy;

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
            gameView.paint.setStyle(Paint.Style.FILL);
            gameView.paint.setColor(Color.WHITE);
            canvas.drawCircle((float) x, (float) y, (float) r, gameView.paint);
        } else {
            gameView.paint.setStyle(Paint.Style.FILL);
            gameView.paint.setColor(color);
            canvas.drawCircle((float) x, (float) y, (float) r, gameView.paint);

            gameView.paint.setStyle(Paint.Style.STROKE);
            gameView.paint.setColor(Color.WHITE);
            gameView.paint.setStrokeWidth(4);
            canvas.drawCircle((float) x, (float) y, (float) r, gameView.paint);
        }

        gameView.resetPaint();
    }

    public void hit() {
        health--;
        if (health <= 0) dead = true;
        hit = true;
        hitTimer = System.nanoTime();
    }

    public void destroy() {
        if (type != TYPE_IMMUNE) dead = true;
    }

    public void explode() {
        if (rank > 1) {
            int amount = 3;

            for (int i = 0; i < amount; i++) {
                Enemy enemy = new Enemy(gameView, state, type, rank - 1);
                enemy.x = this.x;
                enemy.y = this.y;
                double angle;
                if (!ready)
                    angle = Math.random() * 140 + 20;
                else
                    angle = Math.random() * 360;
                enemy.rad = Math.toRadians(angle);
                ((PlayState) state).enemies.add(enemy);
            }
        }
    }

    private void setBaseStats() {
        if (type == TYPE_NORMAL) {
            color = Color.BLUE;
            speed = 2;
            r = 20;
            health = 1;
        }

        if (type == TYPE_FAST) {
            color = Color.GREEN;
            speed = 3;
            r = 20;
            health = 2;
        }

        if (type == TYPE_STRONG) {
            color = Color.MAGENTA;
            speed = 1.5;
            r = 30;
            health = 5;
        }

        if (type == TYPE_IMMUNE) {
            color = Color.YELLOW;
            speed = 2;
            r = 20;
            health = 2;
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

    public void increaseRank() {
        setBaseStats();
        rank++;
        if (rank > maxRank) rank = maxRank;
        setRank();
    }
}
