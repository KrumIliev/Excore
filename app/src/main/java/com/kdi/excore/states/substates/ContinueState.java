package com.kdi.excore.states.substates;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;

import com.kdi.excore.animations.ColorAnimation;
import com.kdi.excore.game.Game;
import com.kdi.excore.states.PlayState;
import com.kdi.excore.states.StateManager;
import com.kdi.excore.utils.Utils;
import com.kdi.excore.utils.ExcoreSharedPreferences;
import com.kdi.excore.xfx.AudioPlayer;

/**
 * Created by Krum Iliev on 5/29/2015.
 */
public class ContinueState extends Substate {

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

    private long continueTimer;
    private long continueLength = 10000;
    private long continueDiff;

    public int numCont;

    private boolean enableSwipe = true;
    private PlayState platState;

    public ContinueState(Game game, StateManager stateManager, PlayState playState) {
        super(game, stateManager);
        color = game.preferences.getSetting(ExcoreSharedPreferences.KEY_TRANS) ? Utils.getRandomColor(false) : Utils.getRandomColor(true);
        timeIsRunning = false;
        this.platState = playState;
        init();
        game.preferences.setSetting(ExcoreSharedPreferences.KEY_MOVE, false);
    }

    private void init() {
        numCont = 3;

        continueString = "- C O N T I N U E  -";
        yesString = "- Y E S -";
        noString = "- N O -";
        continueSubString = "C o n t i n u e   a n d   l o s e";
        continueSub2String = "h a l f   o f   y o u r   s c o r e   s o   f a r";

        timerString = "10";
        exitAnim = new ColorAnimation(game, Color.rgb(150, 0, 0));

        int boundsWidth = game.width / 2 + 100;
        int boundsHeight = game.height / 2 + 100;

        int left = (game.width - boundsWidth) / 2;
        int right = game.width - left;
        int top = (game.height - boundsHeight) / 2;
        int bottom = game.height - top;

        bounds = new Rect(left, top, right, bottom);

        left = bounds.left;
        right = bounds.left + bounds.width() / 2 - 25;
        top = bounds.bottom - 50;
        bottom = bounds.bottom + 50;
        noButton = new Rect(left, top, right, bottom);

        left = bounds.right - bounds.width() / 2 + 25;
        right = bounds.right;
        yesButton = new Rect(left, top, right, bottom);
    }

    @Override
    public boolean update() {
        if (numCont != 0) {

            if (!close && alpha > 250 && !timeIsRunning) {
                timeIsRunning = true;
                continueTimer = System.nanoTime();
            }

            if (continueTimer != 0) {
                continueDiff = (System.nanoTime() - continueTimer) / 1000000;
                if (continueDiff > continueLength) {
                    continueTimer = 0;
                    nextState = new GameOverState(stateManager, game, platState.player.score, platState.waveNumber, platState.player.enemiesKilled);
                    showExitAnim = true;
                }
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
        }


        if (enableSwipe) {
            game.preferences.setSetting(ExcoreSharedPreferences.KEY_MOVE, false);
            enableSwipe = false;
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
            nextState = new GameOverState(stateManager, game, platState.player.score, platState.waveNumber, platState.player.enemiesKilled);
            game.audioPlayer.playSound(AudioPlayer.SOUND_BUTTON);
            showExitAnim = true;
            continueTimer = 0;
        }
    }

    @Override
    public void draw(Canvas canvas) {
        drawBackground(canvas);
        drawContinue(canvas);

        drawButton(canvas, noButton, noString);
        drawButton(canvas, yesButton, yesString);

        flashButton(canvas, noButton, noTimer);
        flashButton(canvas, yesButton, yesTimer);

        if (showExitAnim) exitAnim.draw(canvas);
    }

    private void drawContinue(Canvas canvas) {
        game.paint.setTypeface(game.tf);
        game.paint.setColor(Color.argb(alpha, 255, 255, 255));
        game.paint.setTextAlign(Paint.Align.CENTER);

        game.paint.setTextSize(60);
        canvas.drawText(continueString, game.width / 2, bounds.top, game.paint);

        game.paint.setTextSize(40);
        canvas.drawText("R e m a i n i n g :   " + numCont, game.width / 2, bounds.top + 80, game.paint);

        game.paint.setTextSize(30);
        canvas.drawText(continueSubString, game.width / 2, bounds.bottom - 100, game.paint);

        game.paint.setTextSize(30);
        canvas.drawText(continueSub2String, game.width / 2, bounds.bottom - 80, game.paint);

        if (continueTimer != 0) drawTimer(canvas);

        game.resetPaint();
    }

    private void drawTimer(Canvas canvas) {
        long time = 10 - continueDiff / 1000;
        if (time == 10) {
            timerString = "" + time;
        } else {
            timerString = "0" + time;
        }
        game.paint.setTextSize(120);
        canvas.drawText(timerString, bounds.centerX(), bounds.centerY(), game.paint);
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
        color = game.preferences.getSetting(ExcoreSharedPreferences.KEY_TRANS) ? Utils.getRandomColor(false) : Utils.getRandomColor(true);
        continueTimer = 0;
        timeIsRunning = false;
        timerString = "10";
        game.preferences.setSetting(ExcoreSharedPreferences.KEY_MOVE, true);
        enableSwipe = true;
        numCont--;
    }
}
