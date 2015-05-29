package com.kdi.excore.states.menu;

import android.graphics.Canvas;
import android.graphics.Rect;

import com.kdi.excore.entities.Enemy;
import com.kdi.excore.game.Game;
import com.kdi.excore.states.StateManager;

/**
 * Created by Krum Iliev on 5/25/2015.
 */
public class MainMenuState extends Menu {

    private Rect playButton;
    private Rect optionButton;
    private Rect exitButton;
    private Rect helpButton;
    private Rect rankButton;

    private String strPlay;
    private String strRank;
    private String strOptions;
    private String strHelp;
    private String strExit;

    public MainMenuState(StateManager stateManager, Game game, int color) {
        super(stateManager, game, color, Enemy.TYPE_NORMAL);
        initButtons();
    }

    @Override
    public void handleInput(float x, float y) {
        if (showAnim) return;

        if (playButton.contains((int) x, (int) y)) {
            nextState = new SelectModeState(stateManager, game, anim.color);
            showAnim = true;
        }

        if (rankButton.contains((int) x, (int) y)) {

        }

        if (optionButton.contains((int) x, (int) y)) {
            nextState = new OptionsState(stateManager, game, anim.color);
            showAnim = true;
        }


        if (helpButton.contains((int) x, (int) y)) {

        }

        if (exitButton.contains((int) x, (int) y)) {
            game.litener.onExit();
        }
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);

        drawButton(canvas, playButton, strPlay, null, 40);
        drawButton(canvas, rankButton, strRank, null, 40);
        drawButton(canvas, optionButton, strOptions, null, 40);
        drawButton(canvas, helpButton, strHelp, null, 40);
        drawButton(canvas, exitButton, strExit, null, 40);

        if (showAnim) anim.draw(canvas);
    }

    private void initButtons() {
        strPlay = "- P l a y -";
        strRank = "- R a n k -";
        strOptions = "- O p t i o n s -";
        strHelp = "- H e l p -";
        strExit = "- E x i t -";

        int buttonWidth = game.width / 2 + 100;
        int buttonVerticalSpace = 50;
        int buttonHeight = (game.height - (buttonVerticalSpace * 6)) / 5;

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
        helpButton = new Rect(left, top, right, bottom);

        top = bottom + buttonVerticalSpace;
        bottom = top + buttonHeight;
        exitButton = new Rect(left, top, right, bottom);
    }
}
