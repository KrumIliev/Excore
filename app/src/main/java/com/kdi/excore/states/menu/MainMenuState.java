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
    private Rect creditsButton;
    private Rect googleButton;

    private String strPlay;
    private String strGoogle;
    private String strOptions;
    private String strCredits;

    private String subGoogle;

    private long playTimer;
    private long playDiff;

    private long googleTimer;
    private long googleDiff;

    private long optionsTimer;
    private long optionsDiff;

    private long creditsTimer;
    private long creditsDiff;

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

        if (googleButton.contains((int) x, (int) y)) {
            googleTimer = System.nanoTime();
            game.audioPlayer.playSound(AudioPlayer.SOUND_BUTTON);
            nextState = new GoogleState(stateManager, game, anim.color);
            showAnim = true;
        }

        if (optionButton.contains((int) x, (int) y)) {
            optionsTimer = System.nanoTime();
            game.audioPlayer.playSound(AudioPlayer.SOUND_BUTTON);
            nextState = new OptionsState(stateManager, game, anim.color);
            showAnim = true;
        }


        if (creditsButton.contains((int) x, (int) y)) {
            creditsTimer = System.nanoTime();
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

        if (googleTimer != 0) {
            googleDiff = (System.nanoTime() - googleTimer) / 1000000;
            if (googleDiff > flashInterval) googleTimer = 0;
        }

        if (optionsTimer != 0) {
            optionsDiff = (System.nanoTime() - optionsTimer) / 1000000;
            if (optionsDiff > flashInterval) optionsTimer = 0;
        }

        if (creditsTimer != 0) {
            creditsDiff = (System.nanoTime() - creditsTimer) / 1000000;
            if (creditsDiff > flashInterval) creditsTimer = 0;
        }
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);

        drawButton(canvas, playButton, strPlay, null);
        drawButton(canvas, googleButton, strGoogle, subGoogle);
        drawButton(canvas, optionButton, strOptions, null);
        drawButton(canvas, creditsButton, strCredits, null);

        flashButton(canvas, playButton, playTimer);
        flashButton(canvas, googleButton, googleTimer);
        flashButton(canvas, optionButton, optionsTimer);
        flashButton(canvas, creditsButton, creditsTimer);

        if (showAnim) anim.draw(canvas);
    }

    private void initButtons() {
        strPlay = "- P L A Y -";
        strGoogle = "- G O O G L E  P L A Y -";
        subGoogle = "L e a d e r b o a r d s   a n d   s t u f f";
        strOptions = "- O P T I O N S -";
        strCredits = "- C R E D I T S -";

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
        googleButton = new Rect(left, top, right, bottom);

        top = bottom + buttonVerticalSpace;
        bottom = top + buttonHeight;
        optionButton = new Rect(left, top, right, bottom);

        top = bottom + buttonVerticalSpace;
        bottom = top + buttonHeight;
        creditsButton = new Rect(left, top, right, bottom);
    }
}
