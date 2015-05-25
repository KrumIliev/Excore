package com.kdi.excore.entities;

import android.graphics.Canvas;

import com.kdi.excore.game.Game;

/**
 * Created by Krum Iliev on 5/22/2015.
 */
public abstract class Entity {

    public double x;
    public double y;
    public double r;

    public double dx;
    public double dy;
    public double rad;
    public double speed;

    protected Game gameView;

    public abstract boolean update();

    public abstract void draw(Canvas canvas);
}
