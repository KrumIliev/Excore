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

    private int background;

    private Rect playButton, optionButton, exitButton, helpButton, rankButton;
    private ColorAnimation anim;
    private boolean showAnimation;

    public ArrayList<Enemy> objects;
    private State nextState;

    private String strPlay;
    private String strRank;
    private String strOptions;
    private String strHelp;
    private String strExit;


    public MenuState(StateManager stateManager, Game game, int color) {
        super(stateManager, game);

        background = color;
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

        anim = new ColorAnimation(game, Utils.getRandomColor(false));

        strPlay = "- P l a y -";
        strRank = "- R a n k -";
        strOptions = "- O p t i o n s -";
        strHelp = "- H e l p -";
        strExit = "- E x i t -";

        initObjects();
    }

    @Override
    public void handleInput(float x, float y) {
        if (showAnimation) return;
        showAnimation = true;

        if (playButton.contains((int) x, (int) y))
            nextState = new PlayState(stateManager, game, anim.color);

        if (rankButton.contains((int) x, (int) y)) {

        }
        if (optionButton.contains((int) x, (int) y)) {

        }
        if (helpButton.contains((int) x, (int) y)) {

        }
        if (exitButton.contains((int) x, (int) y)) {

        }
    }

    @Override
    public void update() {
        for (Enemy enemy : objects)
            enemy.update();

        if (showAnimation) {
            boolean remove = anim.update();
            if (remove) {
                showAnimation = false;
                stateManager.setState(nextState);
            }
        }
    }

    @Override
    public void draw(Canvas canvas) {
        canvas.drawColor(background);

        for (Enemy enemy : objects)
            enemy.draw(canvas);

        drawButton(canvas, playButton, strPlay);
        drawButton(canvas, rankButton, strRank);
        drawButton(canvas, optionButton, strOptions);
        drawButton(canvas, helpButton, strHelp);
        drawButton(canvas, exitButton, strExit);

        if (showAnimation) anim.draw(canvas);
    }

    private void drawButton(Canvas canvas, Rect button, String text) {
        game.paint.setStyle(Paint.Style.STROKE);
        game.paint.setColor(Color.WHITE);
        game.paint.setStrokeWidth(2);
        canvas.drawRect(button.left, button.top, button.right, button.bottom, game.paint);

        game.resetPaint();

        game.paint.setTypeface(game.tf);
        game.paint.setTextSize(40);
        game.paint.setColor(Color.WHITE);
        game.paint.setTextAlign(Paint.Align.CENTER);
        int centerY = ((button.bottom - button.top) / 2) + button.top;
        Rect bounds = new Rect();
        game.paint.getTextBounds(text, 0, text.length(), bounds);
        canvas.drawText(text, game.width / 2, centerY - bounds.exactCenterY(), game.paint);

        game.resetPaint();
    }

    private void initObjects() {
        Random random = new Random();
        objects = new ArrayList<>();
        objects.add(new Enemy(game, this, 1, 1, random.nextInt(game.width), random.nextInt(game.height)));
        objects.add(new Enemy(game, this, 1, 1, random.nextInt(game.width), random.nextInt(game.height)));
        objects.add(new Enemy(game, this, 1, 1, random.nextInt(game.width), random.nextInt(game.height)));
        objects.add(new Enemy(game, this, 1, 1, random.nextInt(game.width), random.nextInt(game.height)));
        objects.add(new Enemy(game, this, 1, 1, random.nextInt(game.width), random.nextInt(game.height)));
        objects.add(new Enemy(game, this, 1, 1, random.nextInt(game.width), random.nextInt(game.height)));
        objects.add(new Enemy(game, this, 1, 1, random.nextInt(game.width), random.nextInt(game.height)));
        objects.add(new Enemy(game, this, 1, 1, random.nextInt(game.width), random.nextInt(game.height)));
        objects.add(new Enemy(game, this, 1, 1, random.nextInt(game.width), random.nextInt(game.height)));
    }
}