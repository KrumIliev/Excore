package com.kdi.excore.states.menu;

import android.graphics.Canvas;
import android.graphics.Rect;

import com.kdi.excore.entities.Enemy;
import com.kdi.excore.game.Game;
import com.kdi.excore.states.StateManager;
import com.kdi.excore.utils.ExcoreSharedPreferences;

/**
 * Created by Krum Iliev on 5/27/2015.
 */
public class OptionsState extends Menu {

    private Rect musicButton;
    private Rect soundButton;
    private Rect subsButton;
    private Rect backButton;

    private String musicON, musicOFF;
    private String soundON, soundOFF;
    private String subsON, subsOFF, subsWTF;
    private String back;

    private boolean musicState;
    private boolean soundState;
    private boolean subsState;

    public OptionsState(StateManager stateManager, Game game, int color) {
        super(stateManager, game, color, Enemy.TYPE_STRONG);

        musicState = game.preferences.getSetting(ExcoreSharedPreferences.KEY_MUSIC);
        soundState = game.preferences.getSetting(ExcoreSharedPreferences.KEY_SOUND);
        subsState = game.preferences.getSetting(ExcoreSharedPreferences.KEY_SUBS);

        initButtons();
    }

    @Override
    public void handleInput(float x, float y) {
        if (showAnim) return;

        if (musicButton.contains((int) x, (int) y)) {
            musicState ^= true;
            game.preferences.setSetting(ExcoreSharedPreferences.KEY_MUSIC, musicState);
            setMusicState();
        }
        if (soundButton.contains((int) x, (int) y)) {
            soundState ^= true;
            game.preferences.setSetting(ExcoreSharedPreferences.KEY_SOUND, soundState);
        }
        if (subsButton.contains((int) x, (int) y)) {
            subsState ^= true;
            game.preferences.setSetting(ExcoreSharedPreferences.KEY_SUBS, subsState);
        }
        if (backButton.contains((int) x, (int) y)) {
            nextState = new MainMenuState(stateManager, game, anim.color);
            showAnim = true;
        }
    }

    private void setMusicState() {
        if (musicState) game.audioPlayer.playMusic();
        else game.audioPlayer.stopMusic();
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);

        if (musicState)
            drawButton(canvas, musicButton, musicON, null, 30);
        else
            drawButton(canvas, musicButton, musicOFF, null, 30);

        if (soundState)
            drawButton(canvas, soundButton, soundON, null, 30);
        else
            drawButton(canvas, soundButton, soundOFF, null, 30);

        if (subsState)
            drawButton(canvas, subsButton, subsON, subsWTF, 30);
        else
            drawButton(canvas, subsButton, subsOFF, subsWTF, 30);

        drawButton(canvas, backButton, back, null, 30);

        if (showAnim) anim.draw(canvas);
    }

    private void initButtons() {
        musicON = "- M U S I C     O N -";
        musicOFF = "- M U S I C     O F F -";
        soundON = "- S O U N D     O N -";
        soundOFF = "- S O U N D     O F F -";
        subsON = " - S U B T I T L E S     O N -";
        subsOFF = " - S U B T I T L E S     O F F -";
        subsWTF = "L o l   w u t ?";
        back = "- B A C K -";

        int buttonWidth = game.width / 2 + 100;
        int buttonHeight = 100;
        int buttonVerticalSpace = (game.height - (buttonHeight * 4)) / 5;

        int left = (game.width - buttonWidth) / 2;
        int right = game.width - left;
        int top = buttonVerticalSpace;
        int bottom = top + buttonHeight;
        musicButton = new Rect(left, top, right, bottom);

        top = bottom + buttonVerticalSpace;
        bottom = top + buttonHeight;
        soundButton = new Rect(left, top, right, bottom);

        top = bottom + buttonVerticalSpace;
        bottom = top + buttonHeight;
        subsButton = new Rect(left, top, right, bottom);

        top = bottom + buttonVerticalSpace;
        bottom = top + buttonHeight;
        backButton = new Rect(left, top, right, bottom);
    }
}
