package com.kdi.excore.entities;

import android.graphics.Canvas;
import android.graphics.Color;

import com.kdi.excore.game.Game;

import java.util.Random;

/**
 * Created by Krum Iliev on 5/28/2015.
 */
public class Subtitle extends Entity {

    private String text;
    private int alpha;
    private boolean fade;

    public Subtitle(Game game, String text) {
        this.game = game;

        Random random = new Random();
        this.x = random.nextInt(game.width - 300);
        this.y = random.nextInt(game.height - 200) + 100;

        this.text = text;
        alpha = 0;
    }

    @Override
    public boolean update() {
        if (fade) {
            alpha -= 5;
            if (alpha <= 0) return true;
        } else {
            alpha += 5;
            if (alpha > 250) {
                alpha = 255;
                fade = true;
            }
        }
        return false;
    }

    @Override
    public void draw(Canvas canvas) {
        game.paint.setTypeface(game.tf);
        game.paint.setTextSize(25);
        game.paint.setColor(Color.argb(alpha, 255, 255, 255));
        canvas.drawText(text, (float) x, (float) y, game.paint);
    }
}
