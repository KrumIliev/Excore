package com.kdi.excore.states.menu;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;

import com.kdi.excore.animations.ColorAnimation;
import com.kdi.excore.entities.Enemy;
import com.kdi.excore.game.Game;
import com.kdi.excore.states.State;
import com.kdi.excore.states.StateManager;
import com.kdi.excore.utils.Utils;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by Krum Iliev on 5/29/2015.
 */
public abstract class Menu extends State {

    protected ArrayList<Enemy> objects;
    protected State nextState;

    protected ColorAnimation anim;
    protected boolean showAnim;

    protected int alpha;

    public Menu(StateManager stateManager, Game game, int color, int objectsType) {
        super(stateManager, game);

        background = color;
        alpha = 0;
        objects = new ArrayList<>();
        initObjects(objects, objectsType);
        anim = new ColorAnimation(game, Utils.getRandomColor(false));
    }

    @Override
    public void update() {
        for (Enemy enemy : objects)
            enemy.update();

        alpha += 5;
        if (alpha > 255) alpha = 255;

        if (showAnim) {
            boolean remove = anim.update();
            if (remove) {
                showAnim = false;
                stateManager.setState(nextState);
            }
        }
    }

    @Override
    public void draw(Canvas canvas) {
        canvas.drawColor(background);

        for (Enemy enemy : objects)
            enemy.draw(canvas);
    }

    protected void drawButton(Canvas canvas, Rect button, String text, String subtext, int textSize) {
        game.paint.setStyle(Paint.Style.STROKE);
        game.paint.setColor(Color.argb(alpha, 255, 255, 255));
        game.paint.setStrokeWidth(2);
        canvas.drawRect(button.left, button.top, button.right, button.bottom, game.paint);

        game.resetPaint();

        game.paint.setTypeface(game.tf);
        game.paint.setTextSize(textSize);
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

    protected void initObjects(ArrayList objects, int type) {
        Random random = new Random();
        for (int i = 0; i < 9; i++) {
            if (i % 2 == 0) {
                objects.add(new Enemy(game, this, type, 1, -20, random.nextInt(game.height), 1, false));
            } else {
                objects.add(new Enemy(game, this, type, 1, game.width + 20, random.nextInt(game.height), 1, false));
            }
        }
    }
}
