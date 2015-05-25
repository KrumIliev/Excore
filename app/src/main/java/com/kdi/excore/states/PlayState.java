package com.kdi.excore.states;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;

import com.kdi.excore.animations.Explosion;
import com.kdi.excore.animations.ColorAnimation;
import com.kdi.excore.entities.Bullet;
import com.kdi.excore.entities.Enemy;
import com.kdi.excore.entities.Player;
import com.kdi.excore.entities.PowerUp;
import com.kdi.excore.game.Game;
import com.kdi.excore.utils.Utils;

import java.util.ArrayList;

/**
 * Created by Krum Iliev on 5/25/2015.
 */
public class PlayState extends State {

    private Player player;
    private ArrayList<Bullet> bullets;
    public ArrayList<Enemy> enemies;
    private ArrayList<Explosion> explosions;
    private ArrayList<PowerUp> powerUps;

    private boolean showNextWaveAnimation;
    private ColorAnimation nextWave;

    private long waveStartTimer;
    private long waveStartTimerDiff;
    private int waveNumber;
    private boolean waveStart;
    private int waveDelay = 2000;

    private long slowDownTimer;
    private long slowTimerDiff;
    private int slowDownLength = 6000;

    private long fastTimer;
    private long fastTimerDiff;
    private int fastLength = 6000;

    public int background;

    public boolean pause = false;
    public Rect pauseButton;

    public PlayState(StateManager stateManager, Game game, int color) {
        super(stateManager, game);
        background = color;
        init();
    }

    private void init() {
        waveStartTimer = 0;
        waveStartTimerDiff = 0;
        waveStart = true;
        waveNumber = 0;

        showNextWaveAnimation = false;

        player = new Player(game, this);
        bullets = new ArrayList<>();
        enemies = new ArrayList<>();
        explosions = new ArrayList<>();
        powerUps = new ArrayList<>();
        nextWave = new ColorAnimation(game, Utils.getRandomColor());

        int pauseWidth = 120;
        int pauseHeight = 40;
        int left = game.width / 2 - pauseWidth / 2;
        int right = left + pauseWidth;
        int top = 20;
        int bottom = top + pauseHeight;
        pauseButton = new Rect(left, top, right, bottom);
    }

    @Override
    public void update() {
        if (!pause) {
            updateWave();
            updateNextWaveAnimation();

            // Create enemies
            if (waveStart && enemies.size() == 0) {
                createNewEnemies();
            }

            player.update();
            updateBullets();
            checkBulletEnemyCollision();
            checkPlayerEnemyCollision();
            updateEnemies();
            updatePowerUps();
            checkPlayerPowerUpCollision();
            updateExplosions();
            updateSlowDown();
            updateFastEnemies();
        }
    }

    private void updateWave() {
        if (waveStartTimer == 0 && enemies.size() == 0) {
            waveNumber++;
            waveStart = false;
            waveStartTimer = System.nanoTime();
            showNextWaveAnimation = true;
        } else {
            waveStartTimerDiff = (System.nanoTime() - waveStartTimer) / 1000000;
            if (waveStartTimerDiff > waveDelay) {
                waveStart = true;
                waveStartTimer = 0;
                waveStartTimerDiff = 0;
            }
        }
    }

    private void updateNextWaveAnimation() {
        if (showNextWaveAnimation && waveNumber != 1) {
            boolean remove = nextWave.update();
            if (remove) {
                showNextWaveAnimation = false;
                background = nextWave.color;
                nextWave.reset(Utils.getRandomColor());
            }
        }
    }

    private void updateBullets() {
        for (int i = 0; i < bullets.size(); i++) {
            boolean shouldRemove = bullets.get(i).update();
            if (shouldRemove) {
                bullets.remove(i);
                i--;
            }
        }
    }

    private void updateEnemies() {
        for (int i = 0; i < enemies.size(); i++) {
            Enemy enemy = enemies.get(i);
            if (enemy.dead) {
                addPowerUp(enemy);

                player.addScore((enemy.type + enemy.rank) * 6);
                enemies.remove(i);
                i--;

                enemy.explode();
                explosions.add(new Explosion((float) enemy.x, (float) enemy.y, (int) enemy.r, (int) enemy.r + 30));
            } else {
                enemy.update();
            }
        }
    }

    private void updatePowerUps() {
        for (int i = 0; i < powerUps.size(); i++) {
            boolean remove = powerUps.get(i).update();
            if (remove) {
                powerUps.remove(i);
                i--;
            }
        }
    }

    private void updateExplosions() {
        for (int i = 0; i < explosions.size(); i++) {
            boolean remove = explosions.get(i).update();
            if (remove) {
                explosions.remove(i);
                i--;
            }
        }
    }

    private void updateSlowDown() {
        if (slowDownTimer != 0) {
            slowTimerDiff = (System.nanoTime() - slowDownTimer) / 1000000;
            if (slowTimerDiff > slowDownLength) {
                slowDownTimer = 0;
                for (Enemy enemy : enemies)
                    enemy.slow = false;
            }
        }
    }

    private void updateFastEnemies() {
        if (fastTimer != 0) {
            fastTimerDiff = (System.nanoTime() - fastTimer) / 1000000;
            if (fastTimerDiff > fastLength) {
                fastTimer = 0;
                for (Enemy enemy : enemies)
                    enemy.fast = false;

            }
        }
    }

    private void checkBulletEnemyCollision() {
        for (int i = 0; i < bullets.size(); i++) {
            Bullet bullet = bullets.get(i);
            for (int j = 0; j < enemies.size(); j++) {
                Enemy enemy = enemies.get(j);

                double dx = bullet.x - enemy.x;
                double dy = bullet.y - enemy.y;
                double dist = Math.sqrt(dx * dx + dy * dy);

                if (dist < bullet.r + enemy.r) {
                    enemy.hit();
                    bullets.remove(i);
                    i--;
                    break;
                }
            }
        }
    }

    private void checkPlayerEnemyCollision() {
        if (!player.isRecovering()) {
            for (Enemy enemy : enemies) {
                double dx = player.x - enemy.x;
                double dy = player.y - enemy.y;
                double dist = Math.sqrt(dx * dx + dy * dy);
                if (dist < player.r + enemy.r) {
                    player.loseLife();
                }
            }
        }
    }

    private void checkPlayerPowerUpCollision() {
        for (int i = 0; i < powerUps.size(); i++) {
            PowerUp powerUp = powerUps.get(i);
            double dx = player.x - powerUp.x;
            double dy = player.y - powerUp.y;
            double dist = Math.sqrt(dx * dx + dy * dy);

            if (dist < player.r + powerUp.r) {

                if (powerUp.type == PowerUp.TYPE_LIFE) player.gainLife();
                if (powerUp.type == PowerUp.TYPE_POWER) player.increasePower(1);

                if (powerUp.type == PowerUp.TYPE_SLOW) {
                    slowDownTimer = System.nanoTime();
                    fastTimer = 0;
                    for (Enemy enemy : enemies) {
                        enemy.fast = false;
                        enemy.slow = true;
                    }

                }

                if (powerUp.type == PowerUp.TYPE_DESTROY) {
                    for (Enemy enemy : enemies)
                        enemy.destroy();
                    //TODO set screen to flash red
                }

                if (powerUp.type == PowerUp.TYPE_FASTER_ENEMY) {
                    fastTimer = System.nanoTime();
                    slowDownTimer = 0;
                    for (Enemy enemy : enemies) {
                        enemy.slow = false;
                        enemy.fast = true;
                    }
                }

                if (powerUp.type == PowerUp.TYPE_UPDATE_ENEMY) {
                    for (Enemy enemy : enemies)
                        enemy.increaseRank();
                }

                powerUps.remove(i);
                i--;
            }
        }
    }

    @Override
    public void draw(Canvas canvas) {
        canvas.drawColor(background);
        if (showNextWaveAnimation && waveNumber != 1) nextWave.draw(canvas);

        // Draw wave number
        drawWaveNumber(canvas);
        drawPlayerLives(canvas);
        drawPlayerScore(canvas);
        drawSlowTimer(canvas);
        drawFastTimer(canvas);
        drawPower(canvas);
        drawMenuButton(canvas);

        // Player draw
        player.draw(canvas);

        // Bullets draw
        for (Bullet bullet : bullets)
            bullet.draw(canvas);

        // Enemies draw
        for (Enemy enemy : enemies)
            enemy.draw(canvas);

        // Power up draw
        for (PowerUp powerUp : powerUps)
            powerUp.draw(canvas);

        // Explosion up draw
        for (Explosion explosion : explosions)
            explosion.draw(canvas);
    }

    private void drawWaveNumber(Canvas canvas) {
        if (waveStartTimer != 0) {
            game.paint.setTypeface(game.tf);
            game.paint.setTextSize(50);
            String s = "-   W A V E   " + waveNumber + "   -";
            float length = game.paint.measureText(s);
            int alpha = (int) (255 * Math.sin(3.14 * waveStartTimerDiff / waveDelay));
            if (alpha > 255) alpha = 255;
            game.paint.setColor(Color.argb(alpha, 255, 255, 255));
            canvas.drawText(s, game.width / 2 - length / 2, game.height / 2, game.paint);
            game.resetPaint();
        }
    }

    private void drawPlayerLives(Canvas canvas) {
        for (int i = 0; i < player.lives; i++) {
            game.paint.setStyle(Paint.Style.FILL);
            game.paint.setColor(Color.GRAY);
            canvas.drawCircle(25 + (25 * i), 70, player.r / 2, game.paint);

            game.paint.setStyle(Paint.Style.STROKE);
            game.paint.setColor(Color.WHITE);
            game.paint.setStrokeWidth(2);
            canvas.drawCircle(25 + (25 * i), 70, player.r / 2, game.paint);
            game.resetPaint();
        }
    }

    private void drawPlayerScore(Canvas canvas) {
        game.paint.setTypeface(game.tf);
        game.paint.setTextSize(35);
        game.paint.setColor(Color.WHITE);
        canvas.drawText("Score: " + player.score, 15, 35, game.paint);
        game.resetPaint();
    }

    private void drawSlowTimer(Canvas canvas) {
        if (slowDownTimer != 0) {
            game.paint.setStyle(Paint.Style.FILL);
            game.paint.setColor(Color.GREEN);
            canvas.drawRect(game.width - 150, 50, (float) ((game.width - 50) - 100.0 * slowTimerDiff / slowDownLength), 60, game.paint);

            game.paint.setStyle(Paint.Style.STROKE);
            game.paint.setColor(Color.WHITE);
            game.paint.setStrokeWidth(2);
            canvas.drawRect(game.width - 150, 50, game.width - 50, 60, game.paint);
            game.resetPaint();
        }
    }

    private void drawFastTimer(Canvas canvas) {
        if (fastTimer != 0) {
            game.paint.setStyle(Paint.Style.FILL);
            game.paint.setColor(Color.BLUE);
            canvas.drawRect(game.width - 150, 50, (float) ((game.width - 50) - 100.0 * fastTimerDiff / fastLength), 60, game.paint);

            game.paint.setStyle(Paint.Style.STROKE);
            game.paint.setColor(Color.WHITE);
            game.paint.setStrokeWidth(2);
            canvas.drawRect(game.width - 150, 50, game.width - 50, 60, game.paint);
            game.resetPaint();
        }
    }

    private void drawPower(Canvas canvas) {
        game.paint.setStyle(Paint.Style.FILL);
        int color = Color.WHITE;
        if (player.powerLevel == 1) color = Color.GREEN;
        if (player.powerLevel == 2) color = Color.BLUE;
        if (player.powerLevel == 3) color = Color.YELLOW;
        if (player.powerLevel == 4) color = Color.RED;
        game.paint.setColor(color);
        if (player.powerLevel == 4) {
            canvas.drawRect(game.width - 150, 30, game.width - 50, 40, game.paint);
        } else {
            canvas.drawRect(game.width - 150, 30, (float) (game.width - 150 + 100.0 * player.power / player.getRequiredPower()), 40, game.paint);
        }


        game.paint.setStyle(Paint.Style.STROKE);
        game.paint.setColor(Color.WHITE);
        game.paint.setStrokeWidth(2);
        canvas.drawRect(game.width - 150, 30, game.width - 50, 40, game.paint);
        game.resetPaint();
    }

    private void drawMenuButton(Canvas canvas) {
        game.paint.setStyle(Paint.Style.STROKE);
        game.paint.setColor(Color.WHITE);
        game.paint.setStrokeWidth(2);
        canvas.drawRect(pauseButton.left, pauseButton.top, pauseButton.right, pauseButton.bottom, game.paint);

        game.resetPaint();

        game.paint.setTypeface(game.tf);
        game.paint.setTextSize(25);
        game.paint.setColor(Color.WHITE);
        game.paint.setTextAlign(Paint.Align.CENTER);
        int centerY = ((pauseButton.bottom - pauseButton.top) / 2) + pauseButton.top;
        Rect bounds = new Rect();
        String text = "- M E N U -";
        game.paint.getTextBounds(text, 0, text.length(), bounds);
        canvas.drawText(text, game.width / 2, centerY - bounds.exactCenterY(), game.paint);
    }

    @Override
    public void handleInput(float x, float y) {
        player.setDestination(x, y);
    }

    public void addPowerUp(Enemy enemy) {
        double rand = Math.random();
        if (rand < 0.001)
            powerUps.add(new PowerUp(game, PowerUp.TYPE_LIFE, enemy.x, enemy.y));
        else if (rand < 0.020)
            powerUps.add(new PowerUp(game, PowerUp.TYPE_DESTROY, enemy.x, enemy.y));
        else if (rand < 0.100)
            powerUps.add(new PowerUp(game, PowerUp.TYPE_FASTER_ENEMY, enemy.x, enemy.y));
        else if (rand < 0.120)
            powerUps.add(new PowerUp(game, PowerUp.TYPE_POWER, enemy.x, enemy.y));
        else if (rand < 0.130)
            powerUps.add(new PowerUp(game, PowerUp.TYPE_SLOW, enemy.x, enemy.y));
        else if (rand < 0.140)
            powerUps.add(new PowerUp(game, PowerUp.TYPE_UPDATE_ENEMY, enemy.x, enemy.y));

    }

    public void addBullet(float angle, double x, double y) {
        bullets.add(new Bullet(game, angle, x, y));
    }

    private void createNewEnemies() {
        enemies.clear();

        if (waveNumber == 1) {
            for (int i = 0; i < 4; i++) {
                enemies.add(new Enemy(game, this, 1, 1));
            }
        }

        if (waveNumber == 2) {
            for (int i = 0; i < 4; i++) {
                enemies.add(new Enemy(game, this, 1, 1));
            }
            enemies.add(new Enemy(game, this, 1, 2));
            enemies.add(new Enemy(game, this, 1, 2));
        }

        if (waveNumber == 3) {
            enemies.add(new Enemy(game, this, 1, 3));
            enemies.add(new Enemy(game, this, 1, 3));
            enemies.add(new Enemy(game, this, 1, 4));
        }

        if (waveNumber == 4) {
            for (int i = 0; i < 4; i++) {
                enemies.add(new Enemy(game, this, 2, 1));
            }
        }

        if (waveNumber == 5) {
            for (int i = 0; i < 4; i++) {
                enemies.add(new Enemy(game, this, 2, 1));
            }
            enemies.add(new Enemy(game, this, 2, 2));
            enemies.add(new Enemy(game, this, 2, 2));
        }

        if (waveNumber == 6) {
            enemies.add(new Enemy(game, this, 2, 3));
            enemies.add(new Enemy(game, this, 2, 3));
            enemies.add(new Enemy(game, this, 2, 4));
        }

        if (waveNumber == 7) {
            for (int i = 0; i < 4; i++) {
                enemies.add(new Enemy(game, this, 3, 1));
            }
        }

        if (waveNumber == 8) {
            for (int i = 0; i < 4; i++) {
                enemies.add(new Enemy(game, this, 3, 1));
            }
            enemies.add(new Enemy(game, this, 3, 2));
            enemies.add(new Enemy(game, this, 3, 2));
        }

        if (waveNumber == 9) {
            enemies.add(new Enemy(game, this, 3, 3));
            enemies.add(new Enemy(game, this, 3, 3));
            enemies.add(new Enemy(game, this, 3, 4));
        }
    }
}
