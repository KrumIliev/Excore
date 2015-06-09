package com.kdi.excore.states.menu;

import android.graphics.Canvas;
import android.graphics.Rect;

import com.kdi.excore.entities.Enemy;
import com.kdi.excore.game.Game;
import com.kdi.excore.states.BasicsState;
import com.kdi.excore.states.PlayState;
import com.kdi.excore.states.StateManager;
import com.kdi.excore.xfx.AudioPlayer;

/**
 * Created by Krum Iliev on 5/29/2015.
 */
public class SelectModeState extends Menu {

    private Rect normal;
    private Rect hardcore;
    private Rect time;
    private Rect back;
    private Rect basics;

    private String sNormal;
    private String sHardcore;
    private String sTime;
    private String sBack;
    private String sBasics;

    private String subHardcore;
    private String subTime;
    private String subBasics;

    private long normalTimer;
    private long normalDiff;

    private long hardcoreTimer;
    private long hardcoreDiff;

    private long attackTimer;
    private long attackDiff;

    private long basicsTimer;
    private long basicsDiff;

    private long backTimer;
    private long backDiff;

    public SelectModeState(StateManager stateManager, Game game, int color) {
        super(stateManager, game, color, Enemy.TYPE_IMMUNE);
        initButtons();
    }

    @Override
    public void update() {
        super.update();

        if (normalTimer != 0) {
            normalDiff = (System.nanoTime() - normalTimer) / 1000000;
            if (normalDiff > flashInterval) normalTimer = 0;
        }

        if (hardcoreTimer != 0) {
            hardcoreDiff = (System.nanoTime() - hardcoreTimer) / 1000000;
            if (hardcoreDiff > flashInterval) hardcoreTimer = 0;
        }

        if (attackTimer != 0) {
            attackDiff = (System.nanoTime() - attackTimer) / 1000000;
            if (attackDiff > flashInterval) attackTimer = 0;
        }

        if (backTimer != 0) {
            backDiff = (System.nanoTime() - backTimer) / 1000000;
            if (backDiff > flashInterval) backTimer = 0;
        }

        if (basicsTimer != 0) {
            basicsDiff = (System.nanoTime() - basicsTimer) / 1000000;
            if (basicsDiff > flashInterval) basicsTimer = 0;
        }
    }

    @Override
    public void handleInput(float x, float y) {
        if (showAnim) return;

        if (normal.contains((int) x, (int) y)) {
            normalTimer = System.nanoTime();
            game.audioPlayer.playSound(AudioPlayer.SOUND_BUTTON);
            nextState = new PlayState(stateManager, game, anim.color, PlayState.MODE_NORMAL);
            showAnim = true;
        }

        if (hardcore.contains((int) x, (int) y)) {
            hardcoreTimer = System.nanoTime();
            game.audioPlayer.playSound(AudioPlayer.SOUND_BUTTON);
            nextState = new PlayState(stateManager, game, anim.color, PlayState.MODE_HARDCORE);
            showAnim = true;
        }

        if (time.contains((int) x, (int) y)) {
            attackTimer = System.nanoTime();
            game.audioPlayer.playSound(AudioPlayer.SOUND_BUTTON);
            nextState = new PlayState(stateManager, game, anim.color, PlayState.MODE_TIME_ATTACK);
            showAnim = true;
        }

        if (basics.contains((int) x, (int) y)) {
            basicsTimer = System.nanoTime();
            game.audioPlayer.playSound(AudioPlayer.SOUND_BUTTON);
            nextState = new BasicsState(stateManager, game, anim.color);
            showAnim = true;
        }

        if (back.contains((int) x, (int) y)) {
            backTimer = System.nanoTime();
            game.audioPlayer.playSound(AudioPlayer.SOUND_BUTTON);
            nextState = new MainMenuState(stateManager, game, anim.color);
            showAnim = true;
        }
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);

        drawButton(canvas, normal, sNormal, null);
        drawButton(canvas, hardcore, sHardcore, subHardcore);
        drawButton(canvas, time, sTime, subTime);
        drawButton(canvas, back, sBack, null);
        drawButton(canvas, basics, sBasics, subBasics);

        flashButton(canvas, normal, normalTimer);
        flashButton(canvas, hardcore, hardcoreTimer);
        flashButton(canvas, time, attackTimer);
        flashButton(canvas, back, backTimer);
        flashButton(canvas, basics, basicsTimer);

        if (showAnim) anim.draw(canvas);
    }

    private void initButtons() {
        sNormal = "- N O R M A L -";
        sHardcore = "- H A R D C O R E -";
        subHardcore = "O n l y   1   l i f e";
        sTime = "- T I M E  A T T A C K -";
        subTime = "I f   t h e   t i m e   e n d s   y o u   e n d";
        sBack = "- B A C K -";
        sBasics = "- B A S I C S -";
        subBasics = "H o w   t o   p l a y   a n d   s t u f f   . . .";

        int buttonWidth = game.width / 2 + 100;
        int buttonHeight = 100;
        int buttonVerticalSpace = (game.height - buttonHeight * 5) / 6;

        int left = (game.width - buttonWidth) / 2;
        int right = game.width - left;
        int top = buttonVerticalSpace;
        int bottom = top + buttonHeight;
        normal = new Rect(left, top, right, bottom);

        top = bottom + buttonVerticalSpace;
        bottom = top + buttonHeight;
        hardcore = new Rect(left, top, right, bottom);

        top = bottom + buttonVerticalSpace;
        bottom = top + buttonHeight;
        time = new Rect(left, top, right, bottom);

        top = bottom + buttonVerticalSpace;
        bottom = top + buttonHeight;
        basics = new Rect(left, top, right, bottom);

        top = bottom + buttonVerticalSpace;
        bottom = top + buttonHeight;
        back = new Rect(left, top, right, bottom);
    }
}
