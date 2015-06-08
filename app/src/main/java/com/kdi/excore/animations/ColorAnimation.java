package com.kdi.excore.animations;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import com.kdi.excore.game.Game;

/**
 * Created by Krum Iliev on 5/23/2015.
 */
public class ColorAnimation {

    private float x;
    private float y;
    private int r;
    private int maxR;
    public int color;
    private Game game;

    public ColorAnimation(Game game, int color) {
        this.game = game;
        x = game.getWidth() / 2;
        y = game.getHeight() / 2;
        maxR = game.getWidth() / 2 > game.getHeight() / 2 ? game.getWidth() / 2 : game.getHeight() / 2;
        maxR = maxR + 110;
        reset(color);
    }

    public boolean update() {
        if (game.height > 1000) r += 10;
        else r += 5;
        if (r >= maxR) return true;
        return false;
    }

    public void draw(Canvas canvas) {
        game.paint.setStyle(Paint.Style.FILL);
        game.paint.setColor(color);
        canvas.drawCircle(x, y, r, game.paint);

        game.paint.setStyle(Paint.Style.STROKE);
        game.paint.setColor(Color.WHITE);
        game.paint.setStrokeWidth(4);
        canvas.drawCircle(x, y, r, game.paint);

        game.resetPaint();
    }

    public void reset(int color) {
        this.color = color;
        r = 1;
    }
}
