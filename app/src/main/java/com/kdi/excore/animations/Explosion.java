package com.kdi.excore.animations;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

/**
 * Created by Krum Iliev on 5/23/2015.
 */
public class Explosion {

    private float x;
    private float y;
    private int r;
    private int maxRadius;

    public Explosion(float x, float y, int r, int max) {
        this.x = x;
        this.y = y;
        this.r = r;
        this.maxRadius = max;
    }

    public boolean update() {
        r += 2;
        if (r >= maxRadius) return true;
        return false;
    }

    public void draw(Canvas canvas) {
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.WHITE);
        paint.setStrokeWidth(4);
        canvas.drawCircle(x, y, r, paint);
    }
}
