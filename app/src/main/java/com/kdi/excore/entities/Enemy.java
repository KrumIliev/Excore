package com.kdi.excore.entities;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import com.kdi.excore.game.Game;

/**
 * Created by Krum Iliev on 5/23/2015.
 */
public class Enemy extends Entity {

    public static final int TYPE_NORMAL = 1;
    public static final int TYPE_FAST = 2;
    public static final int TYPE_STRONG = 3;
    public static final int TYPE_IMMUNE = 4;
    public static final int TYPE_BOSS = 5;

    private int health;
    private int type;
    private int rank;

    private boolean ready;
    private boolean dead;

    private boolean hit;
    private long hitTimer;

    private boolean slow;
    private boolean fast;

    private int color;

    private int maxRank = 4;

    public Enemy(Game gameView, int type, int rank) {
        this.type = type;
        this.rank = rank;
        this.gameView = gameView;


        x = Math.random() * gameView.getWidth() / 2 + gameView.getWidth() / 4;
        y = -r;

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
            if (x > r && x < gameView.getWidth() - r && y > r && y < gameView.getHeight() - r)
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
        Paint paint = new Paint();
        paint.setAntiAlias(true);

        if (hit) {
            paint.setStyle(Paint.Style.FILL);
            paint.setColor(Color.WHITE);
            canvas.drawCircle((float) x, (float) y, (float) r, paint);
        } else {
            paint.setStyle(Paint.Style.FILL);
            paint.setColor(color);
            canvas.drawCircle((float) x, (float) y, (float) r, paint);

            paint.setStyle(Paint.Style.STROKE);
            paint.setColor(Color.WHITE);
            paint.setStrokeWidth(4);
            canvas.drawCircle((float) x, (float) y, (float) r, paint);
        }
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
                Enemy enemy = new Enemy(gameView, getType(), getRank() - 1);
                enemy.x = this.x;
                enemy.y = this.y;
                double angle;
                if (!ready)
                    angle = Math.random() * 140 + 20;
                else
                    angle = Math.random() * 360;
                enemy.rad = Math.toRadians(angle);
                gameView.addEnemy(enemy);
            }
        }
    }

    public boolean isDead() {
        return dead;
    }

    public int getType() {
        return type;
    }

    public int getRank() {
        return rank;
    }

    public void setSlow(boolean slow) {
        this.slow = slow;
    }

    public void setFast(boolean fast) {
        this.fast = fast;
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
