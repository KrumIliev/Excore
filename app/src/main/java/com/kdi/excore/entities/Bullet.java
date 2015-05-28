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
        game = view;

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

        if (x < -r || x > game.getWidth() + r || y < -r || y > game.getHeight() + r)
            return true;

        return false;
    }

    @Override
    public void draw(Canvas canvas) {
        game.paint.setStyle(Paint.Style.FILL);
        game.paint.setColor(Color.WHITE);
        canvas.drawCircle((float) x, (float) y, (float) r, game.paint);

        game.resetPaint();
    }
}
