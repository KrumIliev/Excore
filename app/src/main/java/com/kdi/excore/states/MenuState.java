package com.kdi.excore.states;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;

import com.kdi.excore.animations.ColorAnimation;
import com.kdi.excore.entities.Enemy;
import com.kdi.excore.game.Game;
import com.kdi.excore.utils.Utils;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by Krum Iliev on 5/25/2015.
 */
public class MenuState extends State {

    private int buttonWidth;
    private int buttonHeight;
    private int background;

    private Rect playButton, optionButton, exitButton, helpButton, rankButton;
    private ColorAnimation animPlay, animRank, animOptions, animHelp, animExit;
    private boolean play, rank, options, help, exit;

    public ArrayList<Enemy> objects;

    private Random random;

    public MenuState(StateManager stateManager, Game game) {
        super(stateManager, game);

        background = Utils.getRandomColor();
        buttonWidth = game.width / 2;
        int buttonVerticalSpace = 40;
        buttonHeight = (game.height - (buttonVerticalSpace * 6)) / 5;

        random = new Random();
        int randomId = random.nextInt(6);

        animPlay = new ColorAnimation(game, Color.parseColor(getRandomMenuColor(randomId, 1)));
        animRank = new ColorAnimation(game, Color.parseColor(getRandomMenuColor(randomId, 2)));
        animOptions = new ColorAnimation(game, Color.parseColor(getRandomMenuColor(randomId, 3)));
        animHelp = new ColorAnimation(game, Color.parseColor(getRandomMenuColor(randomId, 4)));
        animExit = new ColorAnimation(game, Color.parseColor(getRandomMenuColor(randomId, 5)));

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

        initObjects();
    }

    @Override
    public void handleInput(float x, float y) {
        if (play || rank || options || help || exit) return;
        if (playButton.contains((int) x, (int) y)) play = true;
        if (rankButton.contains((int) x, (int) y)) rank = true;
        if (optionButton.contains((int) x, (int) y)) options = true;
        if (helpButton.contains((int) x, (int) y)) help = true;
        if (exitButton.contains((int) x, (int) y)) exit = true;
    }

    @Override
    public void update() {
        for (Enemy enemy : objects)
            enemy.update();

        if (play) {
            boolean remove = animPlay.update();
            if (remove) {
                play = false;
                stateManager.setState(new PlayState(stateManager, game, animPlay.color));
            }
        }

        if (rank) {
            boolean remove = animRank.update();
            if (remove) {
                rank = false;
                //TODO
            }
        }

        if (options) {
            boolean remove = animOptions.update();
            if (remove) {
                options = false;
                //TODO
            }
        }

        if (help) {
            boolean remove = animHelp.update();
            if (remove) {
                help = false;
                //TODO
            }
        }

        if (exit) {
            boolean remove = animExit.update();
            if (remove) {
                exit = false;
                //TODO
            }
        }
    }

    @Override
    public void draw(Canvas canvas) {
        canvas.drawColor(background);

        for (Enemy enemy : objects)
            enemy.draw(canvas);

        drawButton(canvas, playButton, animPlay, "- P l a y -");
        drawButton(canvas, rankButton, animRank, "- R a n k -");
        drawButton(canvas, optionButton, animOptions, "- O p t i o n s -");
        drawButton(canvas, helpButton, animHelp, "- H e l p -");
        drawButton(canvas, exitButton, animExit, "- E x i t -");

        if (play) animPlay.draw(canvas);
        if (rank) animRank.draw(canvas);
        if (options) animOptions.draw(canvas);
        if (help) animHelp.draw(canvas);
        if (exit) animExit.draw(canvas);
    }

    private void drawButton(Canvas canvas, Rect button, ColorAnimation anim, String text) {
        game.paint.setStyle(Paint.Style.FILL);
        game.paint.setColor(anim.color);
        canvas.drawRect(button.left, button.top, button.right, button.bottom, game.paint);

        game.paint.setStyle(Paint.Style.STROKE);
        game.paint.setColor(Color.WHITE);
        game.paint.setStrokeWidth(3);
        canvas.drawRect(button.left, button.top, button.right, button.bottom, game.paint);

        game.resetPaint();

        game.paint.setTypeface(game.tf);
        game.paint.setTextSize(40);
        game.paint.setColor(Color.WHITE);
        game.paint.setTextAlign(Paint.Align.CENTER);
        int centerX = button.right - button.left;
        int centerY = ((button.bottom - button.top) / 2) + button.top;
        Rect bounds = new Rect();
        game.paint.getTextBounds(text, 0, text.length(), bounds);
        canvas.drawText(text, centerX, centerY - bounds.exactCenterY(), game.paint);

        game.resetPaint();
    }

    private void initObjects() {
        objects = new ArrayList<>();
        objects.add(new Enemy(game, this, 1, 1, random.nextInt(game.width), random.nextInt(game.height)));
        objects.add(new Enemy(game, this, 1, 1, random.nextInt(game.width), random.nextInt(game.height)));
        objects.add(new Enemy(game, this, 1, 1, random.nextInt(game.width), random.nextInt(game.height)));
        objects.add(new Enemy(game, this, 1, 1, random.nextInt(game.width), random.nextInt(game.height)));
        objects.add(new Enemy(game, this, 2, 1, random.nextInt(game.width), random.nextInt(game.height)));
        objects.add(new Enemy(game, this, 2, 1, random.nextInt(game.width), random.nextInt(game.height)));
        objects.add(new Enemy(game, this, 2, 1, random.nextInt(game.width), random.nextInt(game.height)));
        objects.add(new Enemy(game, this, 3, 1, random.nextInt(game.width), random.nextInt(game.height)));
        objects.add(new Enemy(game, this, 3, 1, random.nextInt(game.width), random.nextInt(game.height)));
    }

    private String getRandomMenuColor(int random, int id) {
        switch (random) {
            case 0:
                if (id == 1) return "#1b5e20";
                if (id == 2) return "#2e7d32";
                if (id == 3) return "#388e3c";
                if (id == 4) return "#43a047";
                if (id == 5) return "#4caf50";
                break;
            case 1:
                if (id == 1) return "#4a148c";
                if (id == 2) return "#6a1b9a";
                if (id == 3) return "#7b1fa2";
                if (id == 4) return "#8e24aa";
                if (id == 5) return "#9c27b0";
                break;
            case 2:
                if (id == 1) return "#b71c1c";
                if (id == 2) return "#c62828";
                if (id == 3) return "#d32f2f";
                if (id == 4) return "#e53935";
                if (id == 5) return "#f44336";
                break;
            case 3:
                if (id == 1) return "#0d47a1";
                if (id == 2) return "#1565c0";
                if (id == 3) return "#1976d2";
                if (id == 4) return "#1e88e5";
                if (id == 5) return "#2196f3";
                break;
            case 4:
                if (id == 1) return "#bf360c";
                if (id == 2) return "#d84315";
                if (id == 3) return "#e64a19";
                if (id == 4) return "#f4511e";
                if (id == 5) return "#ff5722";
                break;
            case 5:
                if (id == 1) return "#212121";
                if (id == 2) return "#424242";
                if (id == 3) return "#616161";
                if (id == 4) return "#757575";
                if (id == 5) return "#9e9e9e";
                break;
            case 6:
                if (id == 1) return "#880e4f";
                if (id == 2) return "#ad1457";
                if (id == 3) return "#c2185b";
                if (id == 4) return "#d81b60";
                if (id == 5) return "#e91e63";
                break;
        }
        return "#000000";
    }
}
