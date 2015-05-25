package com.kdi.excore.entities;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import com.kdi.excore.game.Game;

/**
 * Created by Krum Iliev on 5/23/2015.
 */
public class PowerUp extends Entity {

    public static final int TYPE_LIFE = 1;
    public static final int TYPE_POWER = 2;
    public static final int TYPE_SLOW = 3;
    public static final int TYPE_DESTROY = 4;
    public static final int TYPE_UPDATE_ENEMY = 6;
    public static final int TYPE_FASTER_ENEMY = 7;
    public static final int TYPE_IMMORTALITY = 8;
    public static final int TYPE_DOUBLE_SCORE = 9;


    public int type;
    private int color;

    public PowerUp(Game gameView, int type, double x, double y) {
        this.gameView = gameView;
        this.type = type;
        this.x = x;
        this.y = y;

        r = 12;

        if (type == TYPE_LIFE) color = Color.MAGENTA;
        if (type == TYPE_POWER) color = Color.YELLOW;
        if (type == TYPE_SLOW) color = Color.GREEN;
        if (type == TYPE_DESTROY) color = Color.RED;
        if (type == TYPE_UPDATE_ENEMY) color = Color.BLACK;
        if (type == TYPE_FASTER_ENEMY) color = Color.BLUE;
        if (type == TYPE_IMMORTALITY) color = Color.CYAN;
    }

    @Override
    public boolean update() {
        y += 2;
        if (y > gameView.getHeight() + r) return true;
        return false;
    }

    @Override
    public void draw(Canvas canvas) {
        gameView.paint.setStyle(Paint.Style.FILL);
        gameView.paint.setColor(color);
        canvas.drawRect((float) (x - r / 2), (float) (y - r / 2), (float) (x + r / 2), (float) (y + r / 2), gameView.paint);

        gameView.paint.setStyle(Paint.Style.STROKE);
        gameView.paint.setColor(Color.WHITE);
        gameView.paint.setStrokeWidth(3);
        canvas.drawRect((float) (x - r / 2), (float) (y - r / 2), (float) (x + r / 2), (float) (y + r / 2), gameView.paint);

        gameView.resetPaint();
    }
}
