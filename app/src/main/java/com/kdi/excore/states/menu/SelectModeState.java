package com.kdi.excore.states.menu;

import android.graphics.Canvas;
import android.graphics.Rect;

import com.kdi.excore.entities.Enemy;
import com.kdi.excore.game.Game;
import com.kdi.excore.states.PlayState;
import com.kdi.excore.states.StateManager;

/**
 * Created by Krum Iliev on 5/29/2015.
 */
public class SelectModeState extends Menu {

    private Rect normal;
    private Rect survival;
    private Rect hardcore;
    private Rect time;
    private Rect back;

    private String sNormal;
    private String sSurvival;
    private String sHardcore;
    private String sTime;
    private String sBack;

    private String subSurvival;
    private String subHardcore;
    private String subTime;

    public SelectModeState(StateManager stateManager, Game game, int color) {
        super(stateManager, game, color, Enemy.TYPE_IMMUNE);
        initButtons();
    }

    @Override
    public void handleInput(float x, float y) {
        if (showAnim) return;

        if (normal.contains((int) x, (int) y)) {
            nextState = new PlayState(stateManager, game, anim.color, false);
            showAnim = true;
        }

        if (hardcore.contains((int) x, (int) y)) {
            nextState = new PlayState(stateManager, game, anim.color, true);
            showAnim = true;
        }

        if (time.contains((int) x, (int) y)) {

        }


        if (survival.contains((int) x, (int) y)) {

        }

        if (back.contains((int) x, (int) y)) {
            nextState = new MainMenuState(stateManager, game, anim.color);
            showAnim = true;
        }
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);

        drawButton(canvas, normal, sNormal, null, 40);
        drawButton(canvas, hardcore, sHardcore, subHardcore, 40);
        drawButton(canvas, time, sTime, subTime, 40);
        drawButton(canvas, survival, sSurvival, subSurvival, 40);
        drawButton(canvas, back, sBack, null, 40);

        if (showAnim) anim.draw(canvas);
    }

    private void initButtons() {
        sNormal = "- N o r m a l -";
        sHardcore = "- H a r d c o r e -";
        subHardcore = "O n l y   1   l i f e";
        sTime = "- T i m e   A t t a c k -";
        subTime = "I f   t h e   t i m e   e n d s   y o u   e n d";
        sSurvival = "- S u r v i v a l -";
        subSurvival = "N o   s h o o t i n g   o n l y   d o d g i n g";
        sBack = "- B a c k -";

        int buttonWidth = game.width / 2 + 100;
        int buttonVerticalSpace = 50;
        int buttonHeight = (game.height - (buttonVerticalSpace * 6)) / 5;

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
        survival = new Rect(left, top, right, bottom);

        top = bottom + buttonVerticalSpace;
        bottom = top + buttonHeight;
        back = new Rect(left, top, right, bottom);
    }
}
