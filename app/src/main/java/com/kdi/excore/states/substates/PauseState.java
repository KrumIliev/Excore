package com.kdi.excore.states.substates;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;

import com.kdi.excore.game.Game;
import com.kdi.excore.states.StateManager;

/**
 * Created by Krum Iliev on 5/26/2015.
 */
public class PauseState extends Substate {

    public String pauseString;
    public String resumeString;
    public String nextString;
    public String exitString;

    public Rect buttonResume;
    public Rect buttonNext;
    public Rect buttonExit;
    public Rect bounds;

    public PauseState(Game game, StateManager stateManager) {
        super(game, stateManager);
        initPause();
    }

    private void initPause() {
        pauseString = "- P A U S E -";
        resumeString = "- R E S U M E -";
        exitString = "- E X I T -";
        nextString = "- N E X T   S O N G -";

        int buttonWidth = game.width / 2 + 100;
        int buttonHeight = 100;
        int buttonSpace = 50;

        Rect textBounds = new Rect();
        game.paint.getTextBounds(pauseString, 0, pauseString.length(), textBounds);
        int boundsHeight = buttonHeight * 3 + buttonSpace * 4 + textBounds.height();

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
        buttonNext = new Rect(left, top, right, bottom);

        bottom = top - buttonSpace;
        top = bottom - buttonHeight;
        buttonResume = new Rect(left, top, right, bottom);
    }

    @Override
    public void handleInput(float x, float y) {
        if (showExitAnim) return;

        if (buttonResume.contains((int) x, (int) y)) close = true;

        if (buttonExit.contains((int) x, (int) y)) showExitAnim = true;

        if (buttonNext.contains((int) x, (int) y)) game.audioPlayer.nextSong();
    }

    @Override
    public void draw(Canvas canvas) {
        drawBackground(canvas);

        drawPause(canvas);
        drawButton(canvas, buttonResume, resumeString);
        drawButton(canvas, buttonExit, exitString);
        drawButton(canvas, buttonNext, nextString);

            /*
            ONLY FOR DEBUG
            drawButton(canvas, bounds);
            */

        if (showExitAnim) exitAnim.draw(canvas);
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


}
