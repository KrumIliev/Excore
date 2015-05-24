package com.kdi.excore.entities;

import android.graphics.Canvas;

import com.kdi.excore.game.Game;

/**
 * Created by Krum Iliev on 5/22/2015.
 */
public abstract class Entity {

    protected double x;
    protected double y;
    protected double r;

    protected double dx;
    protected double dy;
    protected double rad;
    protected double speed;

    protected Game gameView;

    public abstract boolean update();

    public abstract void draw(Canvas canvas);

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getR() {
        return r;
    }
}
