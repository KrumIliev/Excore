package com.kdi.excore.states;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;

import com.kdi.excore.R;
import com.kdi.excore.animations.ColorAnimation;
import com.kdi.excore.entities.Enemy;
import com.kdi.excore.game.Game;
import com.kdi.excore.utils.ExcoreSharedPreferences;
import com.kdi.excore.utils.Utils;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by Krum Iliev on 5/27/2015.
 */
public class OptionsState extends State {

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

    public ArrayList<Enemy> objects;

    private int alpha;

    private ColorAnimation anim;
    private boolean showAnimation;

    public OptionsState(StateManager stateManager, Game game, int color) {
        super(stateManager, game);

        musicON = "- M U S I C     O N -";
        musicOFF = "- M U S I C     O F F -";
        soundON = "- S O U N D     O N -";
        soundOFF = "- S O U N D     O F F -";
        subsON = " - S U B T I T L E S     O N -";
        subsOFF = " - S U B T I T L E S     O F F -";
        subsWTF = "L o l   w u t ?";
        back = "- B A C K -";

        anim = new ColorAnimation(game, Utils.getRandomColor(false));
        background = color;

        musicState = game.preferences.getSetting(ExcoreSharedPreferences.KEY_MUSIC);
        soundState = game.preferences.getSetting(ExcoreSharedPreferences.KEY_SOUND);
        subsState = game.preferences.getSetting(ExcoreSharedPreferences.KEY_SUBS);

        initButtons();
        initObjects();
    }

    @Override
    public void handleInput(float x, float y) {
        if (showAnimation) return;

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
        if (backButton.contains((int) x, (int) y)) showAnimation = true;
    }

    private void setMusicState() {
        if (musicState) game.audioPlayer.playMusic(R.raw.track_1);
        else game.audioPlayer.stopMusic();
    }

    @Override
    public void update() {
        for (Enemy enemy : objects)
            enemy.update();

        alpha += 5;
        if (alpha > 255) alpha = 255;

        if (showAnimation) {
            boolean remove = anim.update();
            if (remove) {
                showAnimation = false;
                stateManager.setState(new MenuState(stateManager, game, anim.color));
            }
        }
    }

    @Override
    public void draw(Canvas canvas) {
        canvas.drawColor(background);

        for (Enemy enemy : objects)
            enemy.draw(canvas);

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

        drawButton(canvas, backButton, back, null);

        if (showAnimation) anim.draw(canvas);
    }

    private void drawButton(Canvas canvas, Rect button, String text, String subtext) {
        game.paint.setStyle(Paint.Style.STROKE);
        game.paint.setColor(Color.argb(alpha, 255, 255, 255));
        game.paint.setStrokeWidth(2);
        canvas.drawRect(button.left, button.top, button.right, button.bottom, game.paint);

        game.resetPaint();

        game.paint.setTypeface(game.tf);
        game.paint.setTextSize(30);
        game.paint.setColor(Color.argb(alpha, 255, 255, 255));
        game.paint.setTextAlign(Paint.Align.CENTER);
        int centerY = ((button.bottom - button.top) / 2) + button.top;
        Rect bounds = new Rect();
        game.paint.getTextBounds(text, 0, text.length(), bounds);
        canvas.drawText(text, game.width / 2, centerY - bounds.exactCenterY(), game.paint);

        game.resetPaint();

        if (subtext != null) {
            game.paint.setTypeface(game.tf);
            game.paint.setTextSize(20);
            game.paint.setColor(Color.argb(alpha, 255, 255, 255));
            game.paint.setTextAlign(Paint.Align.CENTER);
            game.paint.getTextBounds(text, 0, text.length(), bounds);
            canvas.drawText(subtext, game.width / 2, button.bottom - 12, game.paint);
        }
    }

    private void initButtons() {
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

    private void initObjects() {
        Random random = new Random();
        objects = new ArrayList<>();
        for (int i = 0; i < 9; i++) {
            if (i % 2 == 0) {
                objects.add(new Enemy(game, this, 3, 1, -20, random.nextInt(game.height), 1, false));
            } else {
                objects.add(new Enemy(game, this, 3, 1, game.width + 20, random.nextInt(game.height), 1, false));
            }
        }
    }
}
