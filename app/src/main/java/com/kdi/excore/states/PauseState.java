package com.kdi.excore.states;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;

import com.kdi.excore.animations.ColorAnimation;
import com.kdi.excore.game.Game;
import com.kdi.excore.states.menu.MainMenuState;
import com.kdi.excore.utils.Utils;

/**
 * Created by Krum Iliev on 5/26/2015.
 */
public class PauseState {

    private float x;
    private float y;
    private int r;
    private int maxR;
    public int color;
    private Game game;

    public boolean close, expand;

    public String pauseString;
    public String resumeString;
    public String exitString;

    public Rect buttonResume;
    public Rect buttonExit;
    public Rect bounds;

    private StateManager stateManager;

    private ColorAnimation exitAnim;
    private boolean showExitAnim;

    private int alpha;

    public PauseState(Game game, StateManager stateManager) {
        this.game = game;
        this.stateManager = stateManager;

        x = game.getWidth() / 2;
        y = game.getHeight() / 2;

        maxR = game.getWidth() / 2 > game.getHeight() / 2 ? game.getWidth() / 2 : game.getHeight() / 2;
        maxR = maxR + 110;

        color = Utils.getRandomColor(true);
        r = 1;

        close = false;
        expand = true;

        exitAnim = new ColorAnimation(game, Utils.getRandomColor(false));

        alpha = 0;

        initPause();
    }

    private void initPause() {
        pauseString = "- P A U S E -";
        resumeString = "- R E S U M E -";
        exitString = "- E X I T -";

        int buttonWidth = game.width / 2 + 100;
        int buttonHeight = 100;
        int buttonSpace = 50;

        Rect textBounds = new Rect();
        game.paint.getTextBounds(pauseString, 0, pauseString.length(), textBounds);
        int boundsHeight = buttonHeight * 2 + buttonSpace * 3 + textBounds.height();

        int left = (game.width - buttonWidth) / 2;
        int right = game.width - left;
        int top = (game.height - boundsHeight) / 2;
        int bottom = top + boundsHeight;
        bounds = new Rect(left, top, right, bottom);

        left = (game.width - buttonWidth) / 2;
        right = game.width - left;
        bottom = bounds.bottom;
        top = bottom - buttonHeight;
        buttonExit = new Rect(left, top, right, bottom);

        bottom = top - buttonSpace;
        top = bottom - buttonHeight;
        buttonResume = new Rect(left, top, right, bottom);
    }

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

    public void draw(Canvas canvas) {
        drawBackground(canvas);

        drawPause(canvas);
        drawButton(canvas, buttonResume, resumeString);
        drawButton(canvas, buttonExit, exitString);

            /*
            ONLY FOR DEBUG
            drawButton(canvas, bounds);
            */

        if (showExitAnim) exitAnim.draw(canvas);
    }

    private void drawBackground(Canvas canvas) {
        game.paint.setStyle(Paint.Style.FILL);
        game.paint.setColor(color);
        canvas.drawCircle(x, y, r, game.paint);

        game.paint.setStyle(Paint.Style.STROKE);
        game.paint.setColor(Color.WHITE);
        game.paint.setStrokeWidth(4);
        canvas.drawCircle(x, y, r, game.paint);

        game.resetPaint();
    }

    private void drawButton(Canvas canvas, Rect button, String text) {
        game.paint.setStyle(Paint.Style.STROKE);
        game.paint.setColor(Color.argb(alpha, 255, 255, 255));
        game.paint.setStrokeWidth(2);
        canvas.drawRect(button.left, button.top, button.right, button.bottom, game.paint);

        game.resetPaint();

        game.paint.setTypeface(game.tf);
        game.paint.setTextSize(40);
        game.paint.setColor(Color.argb(alpha, 255, 255, 255));
        game.paint.setTextAlign(Paint.Align.CENTER);
        int centerY = ((button.bottom - button.top) / 2) + button.top;
        Rect bounds = new Rect();
        game.paint.getTextBounds(text, 0, text.length(), bounds);
        canvas.drawText(text, game.width / 2, centerY - bounds.exactCenterY(), game.paint);

        game.resetPaint();
    }

    private void drawPause(Canvas canvas) {
        game.paint.setTypeface(game.tf);
        game.paint.setTextSize(60);
        game.paint.setColor(Color.argb(alpha, 255, 255, 255));
        game.paint.setTextAlign(Paint.Align.CENTER);
        canvas.drawText(pauseString, game.width / 2, bounds.top, game.paint);
        game.resetPaint();
    }

    public void handleInput(float x, float y) {
        if (showExitAnim) return;

        if (buttonResume.contains((int) x, (int) y)) close = true;

        if (buttonExit.contains((int) x, (int) y)) showExitAnim = true;
    }

    public void reset() {
        r = 1;
        close = false;
        expand = true;
        color = Utils.getRandomColor(true);
        alpha = 0;
    }
}
