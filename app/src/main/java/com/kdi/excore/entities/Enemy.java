package com.kdi.excore.entities;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import com.kdi.excore.game.Game;
import com.kdi.excore.states.PlayState;
import com.kdi.excore.states.State;
import com.kdi.excore.utils.Utils;

/**
 * Created by Krum Iliev on 5/23/2015.
 */
public class Enemy extends Entity {

    public static final int TYPE_NORMAL = 1;
    public static final int TYPE_FAST = 2;
    public static final int TYPE_STRONG = 3;
    public static final int TYPE_IMMUNE = 4;

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

    public double multiplier;
    public boolean boss;

    public Enemy(Game gameView, State state, int type, int rank, double multiplier, boolean boss) {
        this.type = type;
        this.rank = rank;
        this.game = gameView;
        this.state = state;
        this.multiplier = multiplier;
        this.boss = boss;

        if (x == 0) {
            x = Math.random() * gameView.width / 2 + gameView.width / 4;
            y = -r;
        }

        setBaseStats();
        setRank();
        if (multiplier != 1) applyMultiplier();

        if (rank == 4) color = Utils.getRandomColor(false);

        double angle = Math.random() * 140 + 20;
        rad = Math.toRadians(angle);

        dx = Math.cos(rad) * speed;
        dy = Math.sin(rad) * speed;

        ready = false;
        dead = false;
        hit = false;
        hitTimer = 0;
    }

    public Enemy(Game gameView, State state, int type, int rank, double x, double y, double multiplier, int color, boolean boss) {
        this(gameView, state, type, rank, multiplier, boss);

        this.x = x;
        this.y = y;
        this.color = color;
    }

    public Enemy(Game gameView, State state, int type, int rank, double x, double y, double multiplier, boolean boss) {
        this(gameView, state, type, rank, multiplier, boss);

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
        if (x > game.getWidth() - r && dx > 0) dx = -dx;
        if (y > game.getHeight() - r && dy > 0) dy = -dy;

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
        if (type != TYPE_IMMUNE && !boss) dead = true;
    }

    public void explode() {
        if (rank > 1) {
            int amount = 3;

            for (int i = 0; i < amount; i++) {
                Enemy enemy = new Enemy(game, state, type, rank - 1, this.x, this.y, this.multiplier, this.color, this.boss);
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
        if (type == TYPE_NORMAL) {
            color = Color.BLUE;
            speed = 2;
            r = 15;
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

    private void applyMultiplier() {
        speed += (multiplier / 2);
        health += (multiplier * rank);
    }

    public void increaseRank() {
        setBaseStats();
        rank++;
        if (rank > maxRank) rank = maxRank;
        setRank();
    }
}
