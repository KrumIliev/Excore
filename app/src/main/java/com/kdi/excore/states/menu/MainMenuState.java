package com.kdi.excore.states.menu;

import android.graphics.Canvas;
import android.graphics.Rect;

import com.kdi.excore.entities.Enemy;
import com.kdi.excore.game.Game;
import com.kdi.excore.states.StateManager;
import com.kdi.excore.states.about.CreditsState;
import com.kdi.excore.xfx.AudioPlayer;

/**
 * Created by Krum Iliev on 5/25/2015.
 */
public class MainMenuState extends Menu {

    private Rect playButton;
    private Rect optionButton;
    private Rect aboutButton;
    private Rect rankButton;

    private String strPlay;
    private String strRank;
    private String strOptions;
    private String strAbout;

    private long playTimer;
    private long playDiff;

    private long rankTimer;
    private long rankDiff;

    private long optionsTimer;
    private long optionsDiff;

    private long aboutTimer;
    private long aboutDiff;

    public MainMenuState(StateManager stateManager, Game game, int color) {
        super(stateManager, game, color, Enemy.TYPE_NORMAL);
        initButtons();
    }

    @Override
    public void handleInput(float x, float y) {
        if (showAnim) return;

        if (playButton.contains((int) x, (int) y)) {
            playTimer = System.nanoTime();
            game.audioPlayer.playSound(AudioPlayer.SOUND_BUTTON);
            nextState = new SelectModeState(stateManager, game, anim.color);
            showAnim = true;
        }

        if (rankButton.contains((int) x, (int) y)) {
            rankTimer = System.nanoTime();
            game.audioPlayer.playSound(AudioPlayer.SOUND_BUTTON);
        }

        if (optionButton.contains((int) x, (int) y)) {
            optionsTimer = System.nanoTime();
            game.audioPlayer.playSound(AudioPlayer.SOUND_BUTTON);
            nextState = new OptionsState(stateManager, game, anim.color);
            showAnim = true;
        }


        if (aboutButton.contains((int) x, (int) y)) {
            aboutTimer = System.nanoTime();
            game.audioPlayer.playSound(AudioPlayer.SOUND_BUTTON);
            nextState = new CreditsState(stateManager, game, anim.color);
            showAnim = true;
        }
    }

    @Override
    public void update() {
        super.update();

        if (playTimer != 0) {
            playDiff = (System.nanoTime() - playTimer) / 1000000;
            if (playDiff > flashInterval) playTimer = 0;
        }

        if (rankTimer != 0) {
            rankDiff = (System.nanoTime() - rankTimer) / 1000000;
            if (rankDiff > flashInterval) rankTimer = 0;
        }

        if (optionsTimer != 0) {
            optionsDiff = (System.nanoTime() - optionsTimer) / 1000000;
            if (optionsDiff > flashInterval) optionsTimer = 0;
        }

        if (aboutTimer != 0) {
            aboutDiff = (System.nanoTime() - aboutTimer) / 1000000;
            if (aboutDiff > flashInterval) aboutTimer = 0;
        }
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);

        drawButton(canvas, playButton, strPlay, null);
        drawButton(canvas, rankButton, strRank, null);
        drawButton(canvas, optionButton, strOptions, null);
        drawButton(canvas, aboutButton, strAbout, null);

        flashButton(canvas, playButton, playTimer);
        flashButton(canvas, rankButton, rankTimer);
        flashButton(canvas, optionButton, optionsTimer);
        flashButton(canvas, aboutButton, aboutTimer);

        if (showAnim) anim.draw(canvas);
    }

    private void initButtons() {
        strPlay = "- P L A Y -";
        strRank = "- R A N K -";
        strOptions = "- O P T I O N S -";
        strAbout = "- C R E D I T S -";

        int buttonWidth = game.width / 2 + 100;
        int buttonHeight = 100;
        int buttonVerticalSpace = (game.height - buttonHeight * 4) / 5;

        int left = (game.width - buttonWidth) / 2;
        int right = game.width - left;
        int top = buttonVerticalSpace;
        int bottom = top + buttonHeight;
        playButton = new Rect(left, top, right, bottom);

        top = bottom + buttonVerticalSpace;
        bottom = top + buttonHeight;
        rankButton = new Rect(left, top, right, bottom);

        top = bottom + buttonVerticalSpace;
        bottom = top + buttonHeight;
        optionButton = new Rect(left, top, right, bottom);

        top = bottom + buttonVerticalSpace;
        bottom = top + buttonHeight;
        aboutButton = new Rect(left, top, right, bottom);
    }
}
