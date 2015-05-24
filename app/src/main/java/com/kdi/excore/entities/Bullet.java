package com.kdi.excore.entities;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import com.kdi.excore.game.Game;

/**
 * Created by Krum Iliev on 5/23/2015.
 */
public class Bullet extends Entity {

    public Bullet(Game view, float angle, double x, double y) {
        gameView = view;

        this.x = x;
        this.y = y;

        r = 3;
        speed = 10;

        rad = Math.toRadians(angle);
        dx = Math.cos(rad) * speed;
        dy = Math.sin(rad) * speed;
    }

    @Override
    public boolean update() {
        x += dx;
        y += dy;

        if (x < -r || x > gameView.getWidth() + r || y < -r || y > gameView.getHeight() + r)
            return true;

        return false;
    }

    @Override
    public void draw(Canvas canvas) {
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.WHITE);
        canvas.drawCircle((float) x, (float) y, (float) r, paint);
    }
}
