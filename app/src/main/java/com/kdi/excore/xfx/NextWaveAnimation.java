package com.kdi.excore.xfx;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import com.kdi.excore.game.Game;

/**
 * Created by Krum Iliev on 5/23/2015.
 */
public class NextWaveAnimation {

    private float x;
    private float y;
    private int r;
    private int maxR;
    private int color;

    public NextWaveAnimation(Game game, int color) {
        x = game.getWidth() / 2;
        y = game.getHeight() / 2;
        maxR = game.getWidth() > game.getHeight() ? game.getWidth() : game.getHeight();
        reset(color);
    }

    public boolean update() {
        r += 5;
        if (r >= maxR) return true;
        return false;
    }

    public void draw(Canvas canvas) {
        Paint paint = new Paint();
        paint.setAntiAlias(true);

        paint.setStyle(Paint.Style.FILL);
        paint.setColor(color);
        canvas.drawCircle(x, y, r, paint);

        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.WHITE);
        paint.setStrokeWidth(4);
        canvas.drawCircle(x, y, r, paint);
    }

    public void reset(int color) {
        this.color = color;
        r = 1;
    }

    public int getColor() {
        return color;
    }
}
