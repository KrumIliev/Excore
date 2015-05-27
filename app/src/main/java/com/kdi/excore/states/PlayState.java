package com.kdi.excore.states;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;

import com.kdi.excore.animations.ColorAnimation;
import com.kdi.excore.animations.Explosion;
import com.kdi.excore.entities.Bullet;
import com.kdi.excore.entities.Enemy;
import com.kdi.excore.entities.Player;
import com.kdi.excore.entities.PowerUp;
import com.kdi.excore.game.Game;
import com.kdi.excore.utils.Utils;
import com.kdi.excore.xfx.AudioPlayer;

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

    public long slowTimer;
    private long slowDiff;
    private int slowTopY;

    public long fastTimer;
    private long fastDiff;
    private int fastTopY;

    private long immortalTimer;
    private long immortalDiff;
    private int immortalTopY;

    private long scoreTimer;
    private long scoreDiff;
    private int scoreTopY;
    private boolean doubleScore;

    private int powerLength = 6000;

    public int background;

    public boolean pause = false;
    public Rect pauseButton;

    private PauseState pauseState;

    private int maxEnemiesOnScreen;

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
        nextWave = new ColorAnimation(game, Utils.getRandomColor(false));

        int pauseWidth = 120;
        int pauseHeight = 40;
        int left = game.width / 2 - pauseWidth / 2;
        int right = left + pauseWidth;
        int top = 20;
        int bottom = top + pauseHeight;
        pauseButton = new Rect(left, top, right, bottom);
        pauseState = new PauseState(game, stateManager);

        slowTopY = 0;
        fastTopY = 0;
        immortalTopY = 0;
        scoreTopY = 0;
        doubleScore = false;

        maxEnemiesOnScreen = 20;
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
            updateImmortal();
            updateScoreTimer();

        } else {
            if (pauseState.update()) {
                pause = false;
                pauseState.reset();
            }
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
                nextWave.reset(Utils.getRandomColor(false));
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

                int score = (enemy.type + enemy.rank) * 6;
                if (doubleScore) {
                    Log.d("PlayState", "Points: " + score);
                    score *= 2;
                    Log.d("PlayState", "Points after power up: " + score);
                }
                player.addScore(score);

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
        if (slowTimer != 0) {
            slowDiff = (System.nanoTime() - slowTimer) / 1000000;
            if (slowDiff > powerLength) {
                slowTimer = 0;
                slowTopY = 0;
                for (Enemy enemy : enemies)
                    enemy.slow = false;
            }
        }
    }

    private void updateFastEnemies() {
        if (fastTimer != 0) {
            fastDiff = (System.nanoTime() - fastTimer) / 1000000;
            if (fastDiff > powerLength) {
                fastTimer = 0;
                fastTopY = 0;
                for (Enemy enemy : enemies)
                    enemy.fast = false;
            }
        }
    }

    private void updateImmortal() {
        if (immortalTimer != 0) {
            immortalDiff = (System.nanoTime() - immortalTimer) / 1000000;
            if (immortalDiff > powerLength) {
                immortalTimer = 0;
                immortalTopY = 0;
                player.immortal = false;
            }
        }
    }

    private void updateScoreTimer() {
        if (scoreTimer != 0) {
            scoreDiff = (System.nanoTime() - scoreTimer) / 1000000;
            if (scoreDiff > powerLength) {
                scoreTimer = 0;
                scoreTopY = 0;
                doubleScore = false;
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
        if (!player.recovering && !player.immortal) {
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
                    game.audioPlayer.playSound(AudioPlayer.POWER_UP_SLOW);
                    slowTimer = System.nanoTime();
                    fastTimer = 0;
                    fastTopY = 0;
                    if (slowTopY == 0) slowTopY = getFreeTimerPosition();
                    for (Enemy enemy : enemies) {
                        enemy.fast = false;
                        enemy.slow = true;
                    }

                }

                if (powerUp.type == PowerUp.TYPE_DESTROY) {
                    game.audioPlayer.playSound(AudioPlayer.POWER_UP_DESTROY);
                    for (Enemy enemy : enemies)
                        enemy.destroy();
                    //TODO set screen to flash red
                }

                if (powerUp.type == PowerUp.TYPE_FASTER_ENEMY) {
                    game.audioPlayer.playSound(AudioPlayer.POWER_UP_FAST);
                    fastTimer = System.nanoTime();
                    slowTimer = 0;
                    slowTopY = 0;
                    if (fastTopY == 0) fastTopY = getFreeTimerPosition();
                    for (Enemy enemy : enemies) {
                        enemy.slow = false;
                        enemy.fast = true;
                    }
                }

                if (powerUp.type == PowerUp.TYPE_UPDATE_ENEMY) {
                    for (Enemy enemy : enemies)
                        enemy.increaseRank();
                }

                if (powerUp.type == PowerUp.TYPE_IMMORTALITY) {
                    immortalTimer = System.nanoTime();
                    player.immortal = true;
                    if (immortalTopY == 0) immortalTopY = getFreeTimerPosition();
                }

                if (powerUp.type == PowerUp.TYPE_DOUBLE_SCORE) {
                    scoreTimer = System.nanoTime();
                    doubleScore = true;
                    if (scoreTopY == 0) scoreTopY = getFreeTimerPosition();
                }

                powerUps.remove(i);
                i--;
            }
        }
    }

    private int getFreeTimerPosition() {
        int position = 40;
        if (slowTopY != position && fastTopY != position && immortalTopY != position && scoreTopY != position)
            return position;
        position = 60;
        if (slowTopY != position && fastTopY != position && immortalTopY != position && scoreTopY != position)
            return position;
        position = 80;
        if (slowTopY != position && fastTopY != position && immortalTopY != position && scoreTopY != position)
            return position;
        return 40;
    }

    @Override
    public void draw(Canvas canvas) {
        canvas.drawColor(background);
        if (showNextWaveAnimation && waveNumber != 1) nextWave.draw(canvas);

        // Draw wave number
        drawWaveNumber(canvas);
        drawPlayerLives(canvas);
        drawPlayerScore(canvas);

        drawTimer(canvas, slowTimer, slowDiff, Color.GREEN, slowTopY);
        drawTimer(canvas, fastTimer, fastDiff, Color.BLUE, fastTopY);
        drawTimer(canvas, immortalTimer, immortalDiff, Color.YELLOW, immortalTopY);
        drawTimer(canvas, scoreTimer, scoreDiff, Color.GRAY, scoreTopY);
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

        if (pause) pauseState.draw(canvas);
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

    private void drawTimer(Canvas canvas, long timer, long diff, int fillColor, int yPosition) {
        if (timer != 0) {
            game.paint.setStyle(Paint.Style.FILL);
            game.paint.setColor(fillColor);
            canvas.drawRect(game.width - 120, yPosition, (float) ((game.width - 20) - 100.0 * diff / powerLength), yPosition + 10, game.paint);

            game.paint.setStyle(Paint.Style.STROKE);
            game.paint.setColor(Color.WHITE);
            game.paint.setStrokeWidth(2);
            canvas.drawRect(game.width - 120, yPosition, game.width - 20, yPosition + 10, game.paint);
            game.resetPaint();
        }
    }

    private void drawPower(Canvas canvas) {
        game.paint.setStyle(Paint.Style.FILL);
        int color = Color.WHITE;
        if (player.powerLevel == 4) color = Color.RED;
        game.paint.setColor(color);
        canvas.drawRect(game.width - 120, 20, (float) (game.width - 120 + 100.0 * player.power / player.getRequiredPower()), 30, game.paint);

        game.paint.setStyle(Paint.Style.STROKE);
        game.paint.setColor(Color.WHITE);
        game.paint.setStrokeWidth(2);
        canvas.drawRect(game.width - 120, 20, game.width - 20, 30, game.paint);
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
        if (pause) {
            pauseState.handleInput(x, y);
        } else if (pauseButton.contains((int) x, (int) y)) {
            pause = true;
        } else {
            player.setDestination(x, y);
        }
    }

    public void addPowerUp(Enemy enemy) {
        double rand = Math.random();
        if (rand < 0.001)
            powerUps.add(new PowerUp(game, PowerUp.TYPE_LIFE, enemy.x, enemy.y));
        else if (rand < 0.020)
            powerUps.add(new PowerUp(game, PowerUp.TYPE_DESTROY, enemy.x, enemy.y));
        else if (rand < 0.050)
            powerUps.add(new PowerUp(game, PowerUp.TYPE_IMMORTALITY, enemy.x, enemy.y));
        else if (rand < 0.070)
            powerUps.add(new PowerUp(game, PowerUp.TYPE_DOUBLE_SCORE, enemy.x, enemy.y));
        else if (rand < 0.100)
            powerUps.add(new PowerUp(game, PowerUp.TYPE_POWER, enemy.x, enemy.y));
        else if (rand < 0.120)
            powerUps.add(new PowerUp(game, PowerUp.TYPE_FASTER_ENEMY, enemy.x, enemy.y));
        else if (rand < 0.130)
            powerUps.add(new PowerUp(game, PowerUp.TYPE_SLOW, enemy.x, enemy.y));
        else if (rand < 0.140)
            powerUps.add(new PowerUp(game, PowerUp.TYPE_UPDATE_ENEMY, enemy.x, enemy.y));
    }

    public void addBullet(float angle, double x, double y) {
        bullets.add(new Bullet(game, angle, x, y));
    }

    public void addEnemy(Enemy enemy) {
        if (slowTimer != 0) enemy.slow = true;
        if (fastTimer != 0) enemy.fast = true;
        enemies.add(enemy);
    }

    private void createNewEnemies() {
        enemies.clear();

        if (waveNumber == 1) {
            for (int i = 0; i < 4; i++) {
                addEnemy(new Enemy(game, this, 1, 1));
            }
        }

        if (waveNumber == 2) {
            for (int i = 0; i < 4; i++) {
                addEnemy(new Enemy(game, this, 1, 1));
            }
            addEnemy(new Enemy(game, this, 1, 2));
            addEnemy(new Enemy(game, this, 1, 2));
        }

        if (waveNumber == 3) {
            addEnemy(new Enemy(game, this, 1, 3));
            addEnemy(new Enemy(game, this, 1, 3));
            addEnemy(new Enemy(game, this, 1, 4));
        }

        if (waveNumber == 4) {
            for (int i = 0; i < 4; i++) {
                addEnemy(new Enemy(game, this, 2, 1));
            }
        }

        if (waveNumber == 5) {
            for (int i = 0; i < 4; i++) {
                addEnemy(new Enemy(game, this, 2, 1));
            }
            addEnemy(new Enemy(game, this, 2, 2));
            addEnemy(new Enemy(game, this, 2, 2));
        }

        if (waveNumber == 6) {
            addEnemy(new Enemy(game, this, 2, 3));
            addEnemy(new Enemy(game, this, 2, 3));
            addEnemy(new Enemy(game, this, 2, 4));
        }

        if (waveNumber == 7) {
            for (int i = 0; i < 4; i++) {
                addEnemy(new Enemy(game, this, 3, 1));
            }
        }

        if (waveNumber == 8) {
            for (int i = 0; i < 4; i++) {
                addEnemy(new Enemy(game, this, 3, 1));
            }
            addEnemy(new Enemy(game, this, 3, 2));
            addEnemy(new Enemy(game, this, 3, 2));
        }

        if (waveNumber == 9) {
            addEnemy(new Enemy(game, this, 3, 3));
            addEnemy(new Enemy(game, this, 3, 3));
            addEnemy(new Enemy(game, this, 3, 4));
        }
    }
}
