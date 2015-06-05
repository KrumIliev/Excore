package com.kdi.excore.states.substates;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.CountDownTimer;

import com.kdi.excore.game.Game;
import com.kdi.excore.states.StateManager;
import com.kdi.excore.utils.ColorUtils;
import com.kdi.excore.utils.ExcoreSharedPreferences;
import com.kdi.excore.xfx.AudioPlayer;

/**
 * Created by Krum Iliev on 5/29/2015.
 */
public class GameOverState extends Substate {

    private String gameOverString;
    private String continueString;
    private String yesString;
    private String noString;
    private String continueSubString;
    private String continueSub2String;
    private String timerString;

    private Rect bounds;
    private Rect yesButton;
    private Rect noButton;

    private boolean timeIsRunning;

    private long noTimer;
    private long noDiff;

    private long yesTimer;
    private long yesDiff;

    private boolean initialUpdate = true;

    private CountDownTimer timer = new CountDownTimer(10000, 1000) {
        @Override
        public void onTick(long l) {
            long seconds = l / 1000;
            if (seconds == 10) {
                timerString = "" + seconds;
            } else {
                timerString = "0" + seconds;
            }

            if (l <= 700) showExitAnim = true;
        }

        @Override
        public void onFinish() {
            showExitAnim = true;
        }
    };

    public GameOverState(Game game, StateManager stateManager) {
        super(game, stateManager);
        color = game.preferences.getSetting(ExcoreSharedPreferences.KEY_TRANS) ? Color.argb(255, 255, 0, 0) : Color.argb(210, 255, 0, 0);
        timeIsRunning = false;
        init();
        game.preferences.setSetting(ExcoreSharedPreferences.KEY_MOVE, false);
    }

    private void init() {
        gameOverString = "- G A M E   O V E R -";
        continueString = "- C O N T I N U E -";
        yesString = "- Y E S -";
        noString = "- N O -";
        continueSubString = "C o n t i n u e   a n d   l o s e";
        continueSub2String = "a l l   y o u r   s c o r e   s o   f a r";
        timerString = "10";

        int boundsWidth = game.width / 2 + 100;
        int boundsHeight = game.height / 2 + 100;

        int left = (game.width - boundsWidth) / 2;
        int right = game.width - left;
        int top = (game.height - boundsHeight) / 2;
        int bottom = game.height - top;

        bounds = new Rect(left, top, right, bottom);

        left = bounds.left;
        right = bounds.left + bounds.width() / 2 - 25;
        top = bounds.bottom - 100;
        bottom = bounds.bottom;
        noButton = new Rect(left, top, right, bottom);

        left = bounds.right - bounds.width() / 2 + 25;
        right = bounds.right;
        yesButton = new Rect(left, top, right, bottom);
    }

    @Override
    public boolean update() {
        if (!close && alpha > 250 && !timeIsRunning) {
            timeIsRunning = true;
            timer.start();
        }

        if (noTimer != 0) {
            noDiff = (System.nanoTime() - noTimer) / 1000000;
            if (noDiff > 100) noTimer = 0;
        }

        if (yesTimer != 0) {
            yesDiff = (System.nanoTime() - yesTimer) / 1000000;
            if (yesDiff > 100) yesTimer = 0;
            game.preferences.setSetting(ExcoreSharedPreferences.KEY_MOVE, true);
        }

        if (initialUpdate) {
            game.preferences.setSetting(ExcoreSharedPreferences.KEY_MOVE, false);
            initialUpdate = false;
        }

        return super.update();
    }

    @Override
    public void handleInput(float x, float y) {
        if (showExitAnim) return;
        if (alpha < 180) return;

        if (yesButton.contains((int) x, (int) y)) {
            yesTimer = System.nanoTime();
            game.audioPlayer.playSound(AudioPlayer.SOUND_BUTTON);
            close = true;
        }

        if (noButton.contains((int) x, (int) y)) {
            noTimer = System.nanoTime();
            game.audioPlayer.playSound(AudioPlayer.SOUND_BUTTON);
            showExitAnim = true;
            timer.cancel();
        }
    }

    @Override
    public void draw(Canvas canvas) {
        drawBackground(canvas);
        drawText(canvas);

        drawButton(canvas, noButton, noString);
        drawButton(canvas, yesButton, yesString);

        flashButton(canvas, noButton, noTimer);
        flashButton(canvas, yesButton, yesTimer);

        if (showExitAnim) exitAnim.draw(canvas);
    }

    private void drawText(Canvas canvas) {
        game.paint.setTypeface(game.tf);
        game.paint.setTextSize(60);
        game.paint.setColor(Color.argb(alpha, 255, 255, 255));
        game.paint.setTextAlign(Paint.Align.CENTER);
        canvas.drawText(gameOverString, game.width / 2, bounds.top, game.paint);

        game.paint.setTextSize(40);
        canvas.drawText(continueString, game.width / 2, bounds.top + 100, game.paint);

        game.paint.setTextSize(30);
        canvas.drawText(continueSubString, game.width / 2, bounds.bottom - 150, game.paint);

        game.paint.setTextSize(30);
        canvas.drawText(continueSub2String, game.width / 2, bounds.bottom - 130, game.paint);

        game.paint.setTextSize(120);
        canvas.drawText(timerString, bounds.centerX(), bounds.centerY(), game.paint);

        game.resetPaint();
    }

    private void drawButton(Canvas canvas, Rect button, String text) {
        game.paint.setStyle(Paint.Style.STROKE);
        game.paint.setColor(Color.argb(alpha, 255, 255, 255));
        game.paint.setStrokeWidth(2);
        canvas.drawRect(button.left, button.top, button.right, button.bottom, game.paint);

        game.resetPaint();

        game.paint.setTypeface(game.tf);
        game.paint.setTextSize(35);
        game.paint.setColor(Color.argb(alpha, 255, 255, 255));
        game.paint.setTextAlign(Paint.Align.CENTER);
        int centerY = ((button.bottom - button.top) / 2) + button.top;
        int centerX = ((button.right - button.left) / 2) + button.left;
        Rect bounds = new Rect();
        game.paint.getTextBounds(text, 0, text.length(), bounds);
        canvas.drawText(text, centerX, centerY - bounds.exactCenterY(), game.paint);

        game.resetPaint();
    }

    @Override
    public void reset() {
        super.reset();
        color = game.preferences.getSetting(ExcoreSharedPreferences.KEY_TRANS) ? Color.argb(255, 255, 0, 0) : Color.argb(210, 255, 0, 0);
        timer.cancel();
        timeIsRunning = false;
        timerString = "10";
        game.preferences.setSetting(ExcoreSharedPreferences.KEY_MOVE, true);
        initialUpdate = true;
    }
}
