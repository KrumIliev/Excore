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
    public static final int TYPE_FLY = 6;
    public static final int TYPE_FASTER_ENEMY = 7;
    public static final int TYPE_IMMORTALITY = 8;
    public static final int TYPE_DOUBLE_SCORE = 9;

    public int type;
    private int color;
    public String text;

    public PowerUp(Game gameView, int type, double x, double y) {
        this.game = gameView;
        this.type = type;
        this.x = x;
        this.y = y;

        r = 12;

        if (type == TYPE_LIFE) {
            color = Color.MAGENTA;
            text = "+ 1   L i f e";
        }
        if (type == TYPE_POWER) {
            color = Color.CYAN;
            text = "+ 1   W e a p o n   P o w e r";
        }
        if (type == TYPE_SLOW) {
            color = Color.GREEN;
            text = "E n e m i e s   s p e e d   d o w n";
        }
        if (type == TYPE_DESTROY) {
            color = Color.RED;
            text = "K i l l   a l l !";
        }
        if (type == TYPE_FLY) {
            color = Color.BLACK;
            text = "K e e p   o n   f l y i n g";
        }
        if (type == TYPE_FASTER_ENEMY) {
            color = Color.BLUE;
            text = "E n e m i e s   s p e e d   u p";
        }
        if (type == TYPE_IMMORTALITY) {
            color = Color.YELLOW;
            text = "G o d   m o d e !";
        }
        if (type == TYPE_DOUBLE_SCORE) {
            color = Color.GRAY;
            text = "S c o r e   x 2";
        }
    }

    @Override
    public boolean update() {
        y += 2;
        if (y > game.getHeight() + r) return true;
        return false;
    }

    @Override
    public void draw(Canvas canvas) {
        game.paint.setStyle(Paint.Style.FILL);
        game.paint.setColor(color);
        canvas.drawRect((float) (x - r / 2), (float) (y - r / 2), (float) (x + r / 2), (float) (y + r / 2), game.paint);

        game.paint.setStyle(Paint.Style.STROKE);
        game.paint.setColor(Color.WHITE);
        game.paint.setStrokeWidth(3);
        canvas.drawRect((float) (x - r / 2), (float) (y - r / 2), (float) (x + r / 2), (float) (y + r / 2), game.paint);

        game.resetPaint();
    }
}
