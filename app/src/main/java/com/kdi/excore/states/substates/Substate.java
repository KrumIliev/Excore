package com.kdi.excore.states.substates;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;

import com.kdi.excore.animations.ColorAnimation;
import com.kdi.excore.game.Game;
import com.kdi.excore.states.StateManager;
import com.kdi.excore.states.menu.MainMenuState;
import com.kdi.excore.utils.ColorUtils;
import com.kdi.excore.utils.ExcoreSharedPreferences;

/**
 * Created by Krum Iliev on 5/29/2015.
 */
public abstract class Substate {

    protected float x;
    protected float y;
    protected int r;
    protected int maxR;
    protected int color;
    protected Game game;

    protected boolean close;
    protected boolean expand;

    protected StateManager stateManager;

    protected ColorAnimation exitAnim;
    protected boolean showExitAnim;

    protected int alpha;

    public Substate(Game game, StateManager stateManager) {
        this.game = game;
        this.stateManager = stateManager;

        x = game.getWidth() / 2;
        y = game.getHeight() / 2;

        maxR = game.getWidth() / 2 > game.getHeight() / 2 ? game.getWidth() / 2 : game.getHeight() / 2;
        maxR = maxR + 110;

        color = game.preferences.getSetting(ExcoreSharedPreferences.KEY_TRANS) ? ColorUtils.getRandomColor(false) : ColorUtils.getRandomColor(true);
        r = 1;

        close = false;
        expand = true;

        exitAnim = new ColorAnimation(game, ColorUtils.getRandomColor(false));

        alpha = 0;
    }

    public abstract void handleInput(float x, float y);

    public abstract void draw(Canvas canvas);

    public boolean update() {
        if (close) {
            r -= 5;
            if (r <= 1) return true;
        } else {
            if (expand) r += 5;
            if (r > maxR / 2) {
                alpha += 3;
                if (alpha > 255) alpha = 255;
            }
            if (r >= maxR) expand = false;
        }

        if (close) {
            alpha -= 7;
            if (alpha < 0) alpha = 0;
        }

        if (showExitAnim) {
            boolean remove = exitAnim.update();
            if (remove) {
                showExitAnim = false;
                stateManager.setState(new MainMenuState(stateManager, game, exitAnim.color));
            }
        }

        return false;
    }

    protected void drawBackground(Canvas canvas) {
        game.paint.setStyle(Paint.Style.FILL);
        game.paint.setColor(color);
        canvas.drawCircle(x, y, r, game.paint);

        game.paint.setStyle(Paint.Style.STROKE);
        game.paint.setColor(Color.WHITE);
        game.paint.setStrokeWidth(4);
        canvas.drawCircle(x, y, r, game.paint);

        game.resetPaint();
    }

    protected void flashButton(Canvas canvas, Rect button, long timer) {
        if (timer != 0) {
            game.paint.setStyle(Paint.Style.FILL);
            game.paint.setColor(Color.argb(alpha, 255, 255, 255));
            canvas.drawRect(button.left, button.top, button.right, button.bottom, game.paint);
            game.resetPaint();
        }
    }

    public void reset() {
        r = 1;
        close = false;
        expand = true;
        color = game.preferences.getSetting(ExcoreSharedPreferences.KEY_TRANS) ? ColorUtils.getRandomColor(false) : ColorUtils.getRandomColor(true);
        alpha = 0;
    }
}
