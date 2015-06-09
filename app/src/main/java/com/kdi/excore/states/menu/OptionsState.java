package com.kdi.excore.states.menu;

import android.graphics.Canvas;
import android.graphics.Rect;

import com.kdi.excore.entities.Enemy;
import com.kdi.excore.game.Game;
import com.kdi.excore.states.StateManager;
import com.kdi.excore.utils.ExcoreSharedPreferences;
import com.kdi.excore.xfx.AudioPlayer;

/**
 * Created by Krum Iliev on 5/27/2015.
 */
public class OptionsState extends Menu {

    private Rect musicButton;
    private Rect soundButton;
    private Rect subsButton;
    private Rect transButton;
    private Rect backButton;

    private String musicON, musicOFF;
    private String soundON, soundOFF;
    private String subsON, subsOFF, subsWTF;
    private String transON, transOFF;
    private String back;

    private boolean musicState;
    private boolean soundState;
    private boolean subsState;
    private boolean transState;

    private long musicTimer;
    private long musicDiff;

    private long soundTimer;
    private long soundDiff;

    private long subsTimer;
    private long subsDiff;

    private long transTimer;
    private long transDiff;

    private long backTimer;
    private long backDiff;

    public OptionsState(StateManager stateManager, Game game, int color) {
        super(stateManager, game, color, Enemy.TYPE_STRONG);

        musicState = game.preferences.getSetting(ExcoreSharedPreferences.KEY_MUSIC);
        soundState = game.preferences.getSetting(ExcoreSharedPreferences.KEY_SOUND);
        subsState = game.preferences.getSetting(ExcoreSharedPreferences.KEY_SUBS);
        transState = game.preferences.getSetting(ExcoreSharedPreferences.KEY_TRANS);

        initButtons();
    }

    @Override
    public void handleInput(float x, float y) {
        if (showAnim) return;

        if (musicButton.contains((int) x, (int) y)) {
            musicTimer = System.nanoTime();
            game.audioPlayer.playSound(AudioPlayer.SOUND_BUTTON);
            musicState ^= true;
            game.preferences.setSetting(ExcoreSharedPreferences.KEY_MUSIC, musicState);
            setMusicState();
        }

        if (soundButton.contains((int) x, (int) y)) {
            soundTimer = System.nanoTime();
            game.audioPlayer.playSound(AudioPlayer.SOUND_BUTTON);
            soundState ^= true;
            game.preferences.setSetting(ExcoreSharedPreferences.KEY_SOUND, soundState);
        }

        if (subsButton.contains((int) x, (int) y)) {
            subsTimer = System.nanoTime();
            game.audioPlayer.playSound(AudioPlayer.SOUND_BUTTON);
            subsState ^= true;
            game.preferences.setSetting(ExcoreSharedPreferences.KEY_SUBS, subsState);
        }

        if (transButton.contains((int) x, (int) y)) {
            transTimer = System.nanoTime();
            game.audioPlayer.playSound(AudioPlayer.SOUND_BUTTON);
            transState ^= true;
            game.preferences.setSetting(ExcoreSharedPreferences.KEY_TRANS, transState);
        }

        if (backButton.contains((int) x, (int) y)) {
            backTimer = System.nanoTime();
            game.audioPlayer.playSound(AudioPlayer.SOUND_BUTTON);
            nextState = new MainMenuState(stateManager, game, anim.color);
            showAnim = true;
        }
    }

    private void setMusicState() {
        if (musicState) game.audioPlayer.playMusic();
        else game.audioPlayer.stopMusic();
    }

    @Override
    public void update() {
        super.update();

        if (musicTimer != 0) {
            musicDiff = (System.nanoTime() - musicTimer) / 1000000;
            if (musicDiff > flashInterval) musicTimer = 0;
        }

        if (soundTimer != 0) {
            soundDiff = (System.nanoTime() - soundTimer) / 1000000;
            if (soundDiff > flashInterval) soundTimer = 0;
        }

        if (subsTimer != 0) {
            subsDiff = (System.nanoTime() - subsTimer) / 1000000;
            if (subsDiff > flashInterval) subsTimer = 0;
        }

        if (transTimer != 0) {
            transDiff = (System.nanoTime() - transTimer) / 1000000;
            if (transDiff > flashInterval) transTimer = 0;
        }

        if (backTimer != 0) {
            backDiff = (System.nanoTime() - backTimer) / 1000000;
            if (backDiff > flashInterval) backTimer = 0;
        }
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);

        if (musicState)
            drawButton(canvas, musicButton, musicON, null);
        else
            drawButton(canvas, musicButton, musicOFF, null);

        if (soundState)
            drawButton(canvas, soundButton, soundON, null);
        else
            drawButton(canvas, soundButton, soundOFF, null);

        if (subsState)
            drawButton(canvas, subsButton, subsON, subsWTF);
        else
            drawButton(canvas, subsButton, subsOFF, subsWTF);

        if (transState)
            drawButton(canvas, transButton, transON, null);
        else
            drawButton(canvas, transButton, transOFF, null);

        drawButton(canvas, backButton, back, null);

        flashButton(canvas, musicButton, musicTimer);
        flashButton(canvas, soundButton, soundTimer);
        flashButton(canvas, subsButton, subsTimer);
        flashButton(canvas, backButton, backTimer);
        flashButton(canvas, transButton, transTimer);

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
        transON = "- D E T A I L S     L O W -";
        transOFF = "- D E T A I L S     H I G H -";

        int buttonWidth = game.width / 2 + 100;
        int buttonHeight = game.height > 1000 ? 150 : 100;
        int buttonVerticalSpace = (game.height - (buttonHeight * 5)) / 6;

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
        transButton = new Rect(left, top, right, bottom);

        top = bottom + buttonVerticalSpace;
        bottom = top + buttonHeight;
        backButton = new Rect(left, top, right, bottom);
    }
}
