package com.kdi.excore.states.about;

import android.graphics.Canvas;
import android.graphics.Color;

import com.kdi.excore.game.Game;

/**
 * Created by Krum Iliev on 5/30/2015.
 */
public class CreditsText {

    public float x;
    public float y;

    public int alpha;

    public String mainText;
    public String subText;

    public Game game;

    public CreditsText(Game game, String mainText, String subText) {
        this.game = game;
        this.mainText = mainText;
        this.subText = subText;

        y = game.height;
        x = game.width / 2;

        alpha = 0;
    }

    public boolean update() {
        if (y > game.height / 2) {
            alpha += 3;
            if (alpha > 250) alpha = 255;
        } else if (y < 100) {
            alpha -= 4;
            if (alpha < 0) {
                alpha = 0;
                return true;
            }
        }

        y -= 2;
        if (y < 0) return true;
        return false;
    }

    public void draw(Canvas canvas) {
        float mainY, subY;
        mainY = y;
        subY = y + 50;
        if (subText == null) mainY = y + 25;

        drawText(canvas, mainText, 40, x, mainY);
        if (subText != null) drawText(canvas, subText, 35, x, subY);
    }

    private void drawText(Canvas canvas, String text, int textSize, float x, float y) {
        game.paint.setTypeface(game.tf);
        game.paint.setTextSize(textSize);
        float length = game.paint.measureText(text);
        game.paint.setColor(Color.argb(alpha, 255, 255, 255));
        canvas.drawText(text, x - length / 2, y, game.paint);
        game.resetPaint();
    }
}
