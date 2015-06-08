package com.kdi.excore.states.substates;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;

import com.kdi.excore.animations.ColorAnimation;
import com.kdi.excore.entities.Enemy;
import com.kdi.excore.game.Game;
import com.kdi.excore.states.State;
import com.kdi.excore.states.StateManager;
import com.kdi.excore.states.menu.MainMenuState;
import com.kdi.excore.utils.ColorUtils;
import com.kdi.excore.utils.ExcoreSharedPreferences;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by Krum Iliev on 6/8/2015.
 */
public class GameOverState extends State {

    private int score;
    private int wave;
    private int enemies;

    private int visibleScore = 0;
    private int visibleEnemies = 0;

    private int total = 0;
    private int visibleTotal = 0;

    private Rect boundsGame;
    private Rect boundsWave;
    private Rect boundsEnemy;
    private Rect boundsScore;
    private Rect boundsFinal;

    private int largeTextHeight;
    private int smallTextHeight;

    private String sGameOver;
    private String sScore;
    private String sWave;
    private String sEnemies;
    private String sFinal;

    private int alpha;
    private int alphaWave;
    private int alphaScore;
    private int alphaEnemies;

    private ColorAnimation anim;
    private boolean showAnim;

    private boolean addEnemies = true;
    private boolean addScore = true;

    private boolean canExit = false;

    private ArrayList<Enemy> objects;

    private int scoreMultiplier;

    public GameOverState(StateManager stateManager, Game game, int score, int wave, int enemies) {
        super(stateManager, game);
        this.score = score;
        this.wave = wave;
        this.enemies = enemies;
        background = Color.RED;
        game.preferences.setSetting(ExcoreSharedPreferences.KEY_MOVE, false);
        init();

        Log.d("Game final", "Score: " + score + " Wave: " + wave + " Enemies: " + enemies);
    }

    private void init() {
        int boundsHeight = game.height / 5;
        boundsGame = new Rect(0, 0, game.width, boundsHeight);
        boundsWave = new Rect(0, boundsGame.bottom, game.width, boundsGame.bottom + boundsHeight);
        boundsEnemy = new Rect(0, boundsWave.bottom, game.width, boundsWave.bottom + boundsHeight);
        boundsScore = new Rect(0, boundsEnemy.bottom, game.width, boundsEnemy.bottom + boundsHeight);
        boundsFinal = new Rect(0, boundsScore.bottom, game.width, boundsScore.bottom + boundsHeight);

        sGameOver = "- G A M E   O V E R -";
        sScore = "- G A M E   S C O R E -";
        sWave = "- W A V E   R E A C H E D -";
        sEnemies = "- C O R E S   D E S T R O Y E D -";
        sFinal = "- F I N A L   S C O R E -";

        alpha = 0;
        alphaScore = 0;
        alphaEnemies = 0;

        anim = new ColorAnimation(game, ColorUtils.getRandomColor(false));

        game.paint.setTypeface(game.tf);
        game.paint.setTextSize(80);
        Rect bounds = new Rect();
        game.paint.getTextBounds(sGameOver, 0, sGameOver.length(), bounds);
        largeTextHeight = bounds.height();
        game.paint.setTextSize(40);
        game.paint.getTextBounds(sGameOver, 0, sGameOver.length(), bounds);
        smallTextHeight = bounds.height();
        game.resetPaint();

        scoreMultiplier = wave / 3;
        if (scoreMultiplier == 0) scoreMultiplier = 1;

        initObjects();
    }

    @Override
    public void update() {
        if (!canExit) {
            alpha += 5;
            if (alpha > 255) alpha = 255;
            if (alpha == 255) alphaWave += 3;

            if (alphaWave > 255) alphaWave = 255;
            if (alphaWave == 255) alphaEnemies += 3;

            if (alphaEnemies > 255) alphaEnemies = 255;
            if (alphaEnemies > 210 && visibleEnemies < enemies) visibleEnemies += 2;
            if (visibleEnemies > enemies) visibleEnemies = enemies;

            if (alphaEnemies == 255) {
                alphaScore += 3;
                if (addEnemies) {
                    total += (wave * enemies);
                    addEnemies = false;
                }
            }
            if (alphaScore > 255) alphaScore = 255;
            if (alphaScore == 255) {
                if (addScore) {
                    total += (scoreMultiplier * score);
                    addScore = false;
                }
            }

            if (alphaScore > 210 && visibleScore < score) {
                if (score - visibleScore > 1000) visibleScore += 100;
                else if (score - visibleScore > 100) visibleScore += 10;
                else visibleScore += 2;
            }
            if (visibleScore > score) visibleScore = score;

            if (visibleTotal < total) {
                if (total - visibleTotal > 1000) visibleTotal += 100;
                else if (total - visibleTotal > 100) visibleTotal += 10;
                else visibleTotal += 2;
            }

            if (visibleTotal > total) visibleTotal = total;
            if (visibleTotal == total && alphaScore == 255) canExit = true;
        }

        for (Enemy enemy : objects)
            enemy.update();

        if (showAnim) {
            boolean remove = anim.update();
            if (remove) {
                showAnim = false;
                stateManager.setState(new MainMenuState(stateManager, game, anim.color));
            }
        }
    }

    @Override
    public void handleInput(float x, float y) {
        if (showAnim) return;

        if (canExit) {
            showAnim = true;
        } else {
            canExit = true;
            alpha = 255;
            alphaWave = 255;
            alphaEnemies = 255;
            alphaScore = 255;
            visibleScore = score;
            visibleEnemies = enemies;
            visibleTotal = scoreMultiplier * score + wave * enemies;
        }
    }

    @Override
    public void draw(Canvas canvas) {
        canvas.drawColor(background);

        for (Enemy enemy : objects)
            enemy.draw(canvas);

        /**
         * Drawing title
         */
        game.paint.setTypeface(game.tf);
        game.paint.setTextSize(60);
        game.paint.setColor(Color.argb(alpha, 255, 255, 255));
        game.paint.setTextAlign(Paint.Align.CENTER);
        canvas.drawText(sGameOver, game.width / 2, boundsGame.centerY(), game.paint);

        /**
         * Drawing final score
         */
        game.paint.setTextSize(40);
        canvas.drawText(sFinal, game.width / 2, boundsFinal.top + smallTextHeight, game.paint);

        game.paint.setTextSize(80);
        canvas.drawText("" + visibleTotal, game.width / 2, boundsFinal.centerY() + largeTextHeight / 2, game.paint);

        /**
         * Drawing wave
         */
        game.paint.setColor(Color.argb(alphaWave, 255, 255, 255));
        game.paint.setTextSize(40);
        canvas.drawText(sWave, game.width / 2, boundsWave.top + smallTextHeight, game.paint);

        game.paint.setTextSize(80);
        canvas.drawText("" + wave, game.width / 2, boundsWave.centerY() + largeTextHeight / 2, game.paint);

        /**
         * Drawing enemies
         */
        game.paint.setColor(Color.argb(alphaEnemies, 255, 255, 255));
        game.paint.setTextSize(40);
        canvas.drawText(sEnemies, game.width / 2, boundsEnemy.top + smallTextHeight, game.paint);

        game.paint.setTextSize(80);
        canvas.drawText("" + visibleEnemies, game.width / 2, boundsEnemy.centerY() + largeTextHeight / 2, game.paint);

        /**
         * Drawing game score
         */
        game.paint.setColor(Color.argb(alphaScore, 255, 255, 255));
        game.paint.setTextSize(40);
        canvas.drawText(sScore, game.width / 2, boundsScore.top + smallTextHeight, game.paint);

        game.paint.setTextSize(80);
        canvas.drawText("" + visibleScore, game.width / 2, boundsScore.centerY() + largeTextHeight / 2, game.paint);

        if (showAnim) anim.draw(canvas);
    }

    private void initObjects() {
        objects = new ArrayList();
        Random random = new Random();
        for (int i = 0; i < 9; i++) {
            if (i % 2 == 0) {
                objects.add(new Enemy(game, this, Enemy.TYPE_NORMAL, 2, -20, random.nextInt(game.height), 1));
            } else {
                objects.add(new Enemy(game, this, Enemy.TYPE_NORMAL, 2, game.width + 20, random.nextInt(game.height), 1));
            }
        }
    }
}
