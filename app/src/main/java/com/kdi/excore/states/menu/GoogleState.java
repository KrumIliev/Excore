package com.kdi.excore.states.menu;

import android.graphics.Canvas;
import android.graphics.Rect;

import com.kdi.excore.R;
import com.kdi.excore.entities.Enemy;
import com.kdi.excore.game.Game;
import com.kdi.excore.states.StateManager;
import com.kdi.excore.xfx.AudioPlayer;

/**
 * Created by Krum Iliev on 6/10/2015.
 */
public class GoogleState extends Menu {

    private Rect lNormal;
    private Rect lHardcore;
    private Rect lTime;
    private Rect achievements;
    private Rect back;

    private String sNormal;
    private String sHardcore;
    private String sTime;
    private String sBack;
    private String sAchievements;

    private long normalTimer;
    private long normalDiff;

    private long hardcoreTimer;
    private long hardcoreDiff;

    private long attackTimer;
    private long attackDiff;

    private long achievementsTimer;
    private long achievementsDiff;

    private long backTimer;
    private long backDiff;

    public GoogleState(StateManager stateManager, Game game, int color) {
        super(stateManager, game, color, Enemy.TYPE_IMMUNE);
        initButtons();
    }

    @Override
    public void handleInput(float x, float y) {
        if (showAnim) return;

        if (lNormal.contains((int) x, (int) y)) {
            normalTimer = System.nanoTime();
            game.audioPlayer.playSound(AudioPlayer.SOUND_BUTTON);
        }

        if (lHardcore.contains((int) x, (int) y)) {
            hardcoreTimer = System.nanoTime();
            game.audioPlayer.playSound(AudioPlayer.SOUND_BUTTON);
        }

        if (lTime.contains((int) x, (int) y)) {
            attackTimer = System.nanoTime();
            game.audioPlayer.playSound(AudioPlayer.SOUND_BUTTON);
        }

        if (achievements.contains((int) x, (int) y)) {
            achievementsTimer = System.nanoTime();
            game.audioPlayer.playSound(AudioPlayer.SOUND_BUTTON);
        }

        if (back.contains((int) x, (int) y)) {
            backTimer = System.nanoTime();
            game.audioPlayer.playSound(AudioPlayer.SOUND_BUTTON);
            nextState = new MainMenuState(stateManager, game, anim.color);
            showAnim = true;
        }
    }

    @Override
    public void update() {
        super.update();

        if (normalTimer != 0) {
            normalDiff = (System.nanoTime() - normalTimer) / 1000000;
            if (normalDiff > flashInterval) {
                normalTimer = 0;
                game.listener.openLeaderboard(game.getContext().getString(R.string.normal_leaderboard));
            }
        }

        if (hardcoreTimer != 0) {
            hardcoreDiff = (System.nanoTime() - hardcoreTimer) / 1000000;
            if (hardcoreDiff > flashInterval) {
                hardcoreTimer = 0;
                game.listener.openLeaderboard(game.getContext().getString(R.string.hardcore_leaderboard));
            }
        }

        if (attackTimer != 0) {
            attackDiff = (System.nanoTime() - attackTimer) / 1000000;
            if (attackDiff > flashInterval) {
                attackTimer = 0;
                game.listener.openLeaderboard(game.getContext().getString(R.string.time_leaderboard));
            }
        }

        if (backTimer != 0) {
            backDiff = (System.nanoTime() - backTimer) / 1000000;
            if (backDiff > flashInterval) backTimer = 0;
        }

        if (achievementsTimer != 0) {
            achievementsDiff = (System.nanoTime() - achievementsTimer) / 1000000;
            if (achievementsDiff > flashInterval) {
                achievementsTimer = 0;
                game.listener.openAchievements();
            }
        }
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);

        drawButton(canvas, lNormal, sNormal, null);
        drawButton(canvas, lHardcore, sHardcore, null);
        drawButton(canvas, lTime, sTime, null);
        drawButton(canvas, back, sBack, null);
        drawButton(canvas, achievements, sAchievements, null);

        flashButton(canvas, lNormal, normalTimer);
        flashButton(canvas, lHardcore, hardcoreTimer);
        flashButton(canvas, lTime, attackTimer);
        flashButton(canvas, back, backTimer);
        flashButton(canvas, achievements, achievementsTimer);

        if (showAnim) anim.draw(canvas);
    }

    private void initButtons() {
        sNormal = "- N O R M A L -";
        sHardcore = "- H A R D C O R E -";
        sTime = "- T I M E  A T T A C K -";
        sBack = "- B A C K -";
        sAchievements = "- A C H I E V E M E N T S -";

        int buttonWidth = game.width / 2 + 100;
        int buttonHeight = 100;
        int buttonVerticalSpace = (game.height - buttonHeight * 5) / 6;

        int left = (game.width - buttonWidth) / 2;
        int right = game.width - left;
        int top = buttonVerticalSpace;
        int bottom = top + buttonHeight;
        lNormal = new Rect(left, top, right, bottom);

        top = bottom + buttonVerticalSpace;
        bottom = top + buttonHeight;
        lHardcore = new Rect(left, top, right, bottom);

        top = bottom + buttonVerticalSpace;
        bottom = top + buttonHeight;
        lTime = new Rect(left, top, right, bottom);

        top = bottom + buttonVerticalSpace;
        bottom = top + buttonHeight;
        achievements = new Rect(left, top, right, bottom);

        top = bottom + buttonVerticalSpace;
        bottom = top + buttonHeight;
        back = new Rect(left, top, right, bottom);
    }
}
