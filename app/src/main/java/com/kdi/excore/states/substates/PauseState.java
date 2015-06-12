package com.kdi.excore.states.substates;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;

import com.kdi.excore.game.Game;
import com.kdi.excore.states.StateManager;
import com.kdi.excore.utils.ExcoreSharedPreferences;
import com.kdi.excore.xfx.AudioPlayer;

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

    private long resumeTimer;
    private long resumeDiff;

    private long nextTimer;
    private long nextDiff;

    private long exitTimer;
    private long exitDiff;

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

        int numButtons = 3;
        boolean showNextSongButton = game.preferences.getSetting(ExcoreSharedPreferences.KEY_MUSIC);
        if (!showNextSongButton) numButtons = 2;

        Rect textBounds = new Rect();
        game.paint.getTextBounds(pauseString, 0, pauseString.length(), textBounds);
        int boundsHeight = buttonHeight * numButtons + buttonSpace * (numButtons + 1) + textBounds.height();

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

        if (showNextSongButton) {
            bottom = top - buttonSpace;
            top = bottom - buttonHeight;
            buttonNext = new Rect(left, top, right, bottom);
        }

        bottom = top - buttonSpace;
        top = bottom - buttonHeight;
        buttonResume = new Rect(left, top, right, bottom);
    }

    @Override
    public void handleInput(float x, float y) {
        if (showExitAnim) return;
        if (alpha < 180) return;

        if (buttonResume.contains((int) x, (int) y)) {
            resumeTimer = System.nanoTime();
            game.audioPlayer.playSound(AudioPlayer.SOUND_BUTTON);
            close = true;
            game.preferences.setSetting(ExcoreSharedPreferences.KEY_MOVE, true);
        }

        if (buttonExit.contains((int) x, (int) y)) {
            exitTimer = System.nanoTime();
            game.audioPlayer.playSound(AudioPlayer.SOUND_BUTTON);
            showExitAnim = true;
        }

        if (game.preferences.getSetting(ExcoreSharedPreferences.KEY_MUSIC)) {
            if (buttonNext.contains((int) x, (int) y)) {
                nextTimer = System.nanoTime();
                game.audioPlayer.playSound(AudioPlayer.SOUND_BUTTON);
                game.audioPlayer.playMusic();
            }
        }
    }

    @Override
    public boolean update() {
        if (resumeTimer != 0) {
            resumeDiff = (System.nanoTime() - resumeTimer) / 1000000;
            if (resumeDiff > 100) resumeTimer = 0;
        }

        if (nextTimer != 0) {
            nextDiff = (System.nanoTime() - nextTimer) / 1000000;
            if (nextDiff > 100) nextTimer = 0;
        }

        if (exitTimer != 0) {
            exitDiff = (System.nanoTime() - exitTimer) / 1000000;
            if (exitDiff > 100) exitTimer = 0;
        }

        return super.update();
    }

    @Override
    public void draw(Canvas canvas) {
        drawBackground(canvas);

        boolean showNextSong = game.preferences.getSetting(ExcoreSharedPreferences.KEY_MUSIC);

        drawPause(canvas);
        drawButton(canvas, buttonResume, resumeString);
        drawButton(canvas, buttonExit, exitString);
        if (showNextSong) drawButton(canvas, buttonNext, nextString);

        flashButton(canvas, buttonResume, resumeTimer);
        flashButton(canvas, buttonExit, exitTimer);
        if (showNextSong) flashButton(canvas, buttonNext, nextTimer);

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
