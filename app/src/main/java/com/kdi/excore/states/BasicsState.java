package com.kdi.excore.states;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;

import com.kdi.excore.R;
import com.kdi.excore.animations.ColorAnimation;
import com.kdi.excore.game.Game;
import com.kdi.excore.states.menu.SelectModeState;
import com.kdi.excore.utils.ColorUtils;
import com.kdi.excore.xfx.AudioPlayer;

/**
 * Created by Krum Iliev on 6/3/2015.
 */
public class BasicsState extends State {

    private ColorAnimation anim;
    private boolean showAnim;
    private int alpha;
    private Bitmap tapImage;

    private Rect moveBounds;
    private Rect powerUpBounds;
    private Rect enemyBounds;

    public BasicsState(StateManager stateManager, Game game, int color) {
        super(stateManager, game);

        background = color;
        alpha = 0;
        anim = new ColorAnimation(game, ColorUtils.getRandomColor(false));
        tapImage = BitmapFactory.decodeResource(game.getResources(), R.drawable.tap);

        // Separates the screen to 3 rect objects with the same size
        moveBounds = new Rect(0, 0, game.width, game.height / 3);
        powerUpBounds = new Rect(0, moveBounds.bottom, game.width, moveBounds.bottom + game.height / 3);
        enemyBounds = new Rect(0, powerUpBounds.bottom, game.width, game.height);
    }

    @Override
    public void update() {
        alpha += 5;
        if (alpha > 255) alpha = 255;

        if (showAnim) {
            boolean remove = anim.update();
            if (remove) {
                showAnim = false;
                stateManager.setState(new SelectModeState(stateManager, game, anim.color));
            }
        }
    }

    @Override
    public void draw(Canvas canvas) {
        canvas.drawColor(background);

        drawMovement(canvas);
        drawPowerUps(canvas);
        drawEnemies(canvas);

        if (showAnim) anim.draw(canvas);
    }

    private void drawMovement(Canvas canvas) {
        game.paint.setAlpha(alpha);
        float imageX = (game.width - tapImage.getWidth()) / 2;
        canvas.drawBitmap(tapImage, imageX, moveBounds.top + 40, game.paint);
        game.resetPaint();

        game.paint.setTypeface(game.tf);
        game.paint.setTextSize(40);
        game.paint.setColor(Color.argb(alpha, 255, 255, 255));
        game.paint.setTextAlign(Paint.Align.CENTER);
        float textY = moveBounds.top + tapImage.getHeight() + 80;
        canvas.drawText("Tap on screen", game.width / 2, textY, game.paint);
        canvas.drawText("to move your core", game.width / 2, textY + 30, game.paint);
        game.resetPaint();
    }

    private void drawPowerUps(Canvas canvas) {
        float powerUpSize = 12;
        float powerUpSpace = ((game.width - powerUpSize * 8) / 9) + ((game.width - powerUpSize * 8) % 9);

        for (int i = 1; i < 9; i++)
            drawPowerUp(canvas, powerUpSpace * i + (powerUpSize / 2) * i, powerUpBounds.top + 20, powerUpSize, getPowerUpColor(i));

        game.paint.setTypeface(game.tf);
        game.paint.setTextSize(40);
        game.paint.setColor(Color.argb(alpha, 255, 255, 255));
        game.paint.setTextAlign(Paint.Align.CENTER);
        float textY = powerUpBounds.top + powerUpSize + 60;
        canvas.drawText("Collect PowerUps", game.width / 2, textY, game.paint);
        canvas.drawText("Some are positive", game.width / 2, textY + 30, game.paint);
        canvas.drawText("and some are negative", game.width / 2, textY + 60, game.paint);
        canvas.drawText("Find out which is which", game.width / 2, textY + 90, game.paint);
        game.resetPaint();
    }

    private void drawPowerUp(Canvas canvas, float x, float y, float size, int color) {
        game.paint.setStyle(Paint.Style.FILL);
        game.paint.setColor(color);
        game.paint.setAlpha(alpha);
        canvas.drawRect(x - size / 2, y - size / 2, x + size / 2, y + size / 2, game.paint);

        game.paint.setStyle(Paint.Style.STROKE);
        game.paint.setColor(Color.WHITE);
        game.paint.setStrokeWidth(3);
        game.paint.setAlpha(alpha);
        canvas.drawRect(x - size / 2, y - size / 2, x + size / 2, y + size / 2, game.paint);

        game.resetPaint();
    }

    private int getPowerUpColor(int index) {
        switch (index) {
            case 1:
                return Color.MAGENTA;
            case 2:
                return Color.CYAN;
            case 3:
                return Color.GREEN;
            case 4:
                return Color.RED;
            case 5:
                return Color.BLACK;
            case 6:
                return Color.BLUE;
            case 7:
                return Color.YELLOW;
            case 8:
                return Color.GRAY;
            default:
                return Color.MAGENTA;
        }
    }

    private void drawEnemies(Canvas canvas) {
        float enemySize = 20;
        float enemySpace = ((game.width - enemySize * 4) / 5) + ((game.width - enemySize * 4) % 5);

        drawEnemy(canvas, enemySpace + enemySize / 2, enemyBounds.top, enemySize, Color.BLUE);
        drawEnemy(canvas, enemySpace * 2 + enemySize / 2 + enemySize, enemyBounds.top, enemySize, Color.GREEN);
        drawEnemy(canvas, enemySpace * 3 + enemySize / 2 + enemySize * 2, enemyBounds.top, enemySize, Color.MAGENTA);
        drawEnemy(canvas, enemySpace * 4 + enemySize / 2 + enemySize * 3, enemyBounds.top, enemySize, Color.YELLOW);

        game.paint.setTypeface(game.tf);
        game.paint.setTextSize(40);
        game.paint.setColor(Color.argb(alpha, 255, 255, 255));
        game.paint.setTextAlign(Paint.Align.CENTER);
        float textY = enemyBounds.top + enemySize + 50;
        canvas.drawText("Destroy other cores and gain points", game.width / 2, textY, game.paint);

        game.paint.setColor(Color.BLUE);
        game.paint.setAlpha(alpha);
        canvas.drawText("Nothing special core", game.width / 2, textY + 30, game.paint);

        game.paint.setColor(Color.GREEN);
        game.paint.setAlpha(alpha);
        canvas.drawText("Extra fast core", game.width / 2, textY + 60, game.paint);

        game.paint.setColor(Color.MAGENTA);
        game.paint.setAlpha(alpha);
        canvas.drawText("Very strong core", game.width / 2, textY + 90, game.paint);

        game.paint.setColor(Color.YELLOW);
        game.paint.setAlpha(alpha);
        canvas.drawText("No PowerUp effect core", game.width / 2, textY + 120, game.paint);
        game.resetPaint();
    }

    private void drawEnemy(Canvas canvas, float x, float y, float r, int color) {
        game.paint.setStyle(Paint.Style.FILL);
        game.paint.setColor(color);
        game.paint.setAlpha(alpha);
        canvas.drawCircle(x, y, r, game.paint);

        game.paint.setStyle(Paint.Style.STROKE);
        game.paint.setColor(Color.WHITE);
        game.paint.setStrokeWidth(4);
        game.paint.setAlpha(alpha);
        canvas.drawCircle(x, y, r, game.paint);

        game.resetPaint();
    }

    @Override
    public void handleInput(float x, float y) {
        if (showAnim) return;
        game.audioPlayer.playSound(AudioPlayer.SOUND_BUTTON);
        showAnim = true;
    }
}
