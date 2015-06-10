package com.kdi.excore.states;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;

import com.kdi.excore.R;
import com.kdi.excore.animations.ColorAnimation;
import com.kdi.excore.animations.Explosion;
import com.kdi.excore.entities.Bullet;
import com.kdi.excore.entities.Enemy;
import com.kdi.excore.entities.Player;
import com.kdi.excore.entities.PowerUp;
import com.kdi.excore.entities.Subtitle;
import com.kdi.excore.game.Game;
import com.kdi.excore.states.substates.ContinueState;
import com.kdi.excore.states.substates.GameOverState;
import com.kdi.excore.states.substates.PauseState;
import com.kdi.excore.utils.Achievements;
import com.kdi.excore.utils.Utils;
import com.kdi.excore.utils.ExcoreSharedPreferences;
import com.kdi.excore.xfx.AudioPlayer;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by Krum Iliev on 5/25/2015.
 */
public class PlayState extends State {

    public static int MODE_NORMAL = 0;
    public static int MODE_HARDCORE = 1;
    public static int MODE_TIME_ATTACK = 2;

    private ColorAnimation exitAnim;
    private boolean showExitAnim;

    public Player player;
    private ArrayList<Bullet> bullets;
    public ArrayList<Enemy> enemies;
    private ArrayList<Explosion> explosions;
    private ArrayList<PowerUp> powerUps;
    private ArrayList<Subtitle> subtitles;

    private boolean showNextWaveAnimation;
    private ColorAnimation nextWave;

    private long waveStartTimer;
    private long waveStartTimerDiff;
    public int waveNumber;
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
    private int scoreMultiplier;

    private long flyTimer;
    private long flyDiff;
    private int flyTopY;

    private int powerLength = 6000;

    public boolean pause = false;
    public Rect pauseButton;
    private long pauseTimer;
    private long pauseDiff;

    private PauseState pauseState;
    private ContinueState continueState;

    private int maxTypeOneWave;
    private int maxTypeTwoWave;
    private int maxTypeThreeWave;

    public int mode;

    private String countdownTimerString;
    private long countdownTimerLength = 30000;
    private long countdownTimer;
    private long countdownDiff;

    /**
     * Achievement triggers
     */
    public Achievements achievements;
    public boolean lucky = true;
    public boolean updatesWeak = true;

    public PlayState(StateManager stateManager, Game game, int color, int mode) {
        super(stateManager, game);
        background = color;
        this.mode = mode;
        achievements = new Achievements();
        init();
        game.preferences.setSetting(ExcoreSharedPreferences.KEY_MOVE, true);
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
        subtitles = new ArrayList<>();
        nextWave = new ColorAnimation(game, Utils.getRandomColor(false));

        int pauseWidth = 120;
        int pauseHeight = 40;
        int left = game.width / 2 - pauseWidth / 2;
        int right = left + pauseWidth;
        int top = 20;
        int bottom = top + pauseHeight;
        pauseButton = new Rect(left, top, right, bottom);
        pauseState = new PauseState(game, stateManager);
        continueState = new ContinueState(game, stateManager, this);

        slowTopY = 0;
        fastTopY = 0;
        immortalTopY = 0;
        scoreTopY = 0;
        flyTopY = 0;

        maxTypeOneWave = 15;
        maxTypeTwoWave = 7;
        maxTypeThreeWave = 3;

        scoreMultiplier = 1;

        exitAnim = new ColorAnimation(game, Color.rgb(150, 0, 0));
    }

    @Override
    public void update() {
        if (!pause && !player.dead) {
            updateWave();
            updateNextWaveAnimation();

            // Create enemies
            if (waveStart && enemies.size() == 0) {
                generateWave();
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
            updateSubtitles();
            updateFly();
            if (mode == MODE_TIME_ATTACK) updateCountDown();

            if (pauseTimer != 0) {
                pauseDiff = (System.nanoTime() - pauseTimer) / 1000000;
                if (pauseDiff > 100) {
                    pauseTimer = 0;
                    pause = true;
                }
            }

            checkAchievements();
        }

        if (pause) {
            if (pauseState.update()) {
                pause = false;
                pauseState.reset();
            }
        }

        if (player.dead) {
            if (continueState.numCont == 0 || mode == MODE_HARDCORE) {
                showExitAnim = true;
            } else if (continueState.update()) {
                continueState.reset();
                player.continueGame();
                if (mode == MODE_TIME_ATTACK) {
                    countdownDiff = 0;
                    countdownTimer = System.nanoTime();
                }
            }
        }

        if (showExitAnim) {
            boolean remove = exitAnim.update();
            if (remove) {
                showExitAnim = false;
                stateManager.setState(new GameOverState(stateManager, game, player.score, waveNumber, player.enemiesKilled, mode, achievements));
            }
        }
    }

    private void updateCountDown() {
        if (countdownTimer != 0) {
            countdownDiff = (System.nanoTime() - countdownTimer) / 1000000;
            long length = waveNumber % 10 == 0 ? countdownTimerLength * 2 : countdownTimerLength;
            if (countdownDiff > length) {
                if (!enemies.isEmpty()) player.dead = true;
            }
        }
    }

    private void updateWave() {
        if (waveStartTimer == 0 && enemies.size() == 0) {
            waveNumber++;
            waveStart = false;
            waveStartTimer = System.nanoTime();
            showNextWaveAnimation = true;
            if (mode == MODE_TIME_ATTACK) {
                countdownDiff = (System.nanoTime() - countdownTimer) / 1000000;
                if (countdownDiff < 10000) achievements.theFlash = true;
                countdownDiff = 0;
                countdownTimer = System.nanoTime();
            }

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

                int typeMultiplier = 6;
                if (enemy.type == Enemy.TYPE_BOSS) typeMultiplier = 16;
                int waveMultiplier = waveNumber / 10 + 1;
                player.addScore((((enemy.type + enemy.rank) * typeMultiplier) * waveMultiplier) * scoreMultiplier);
                player.enemiesKilled++;
                game.audioPlayer.playSound(AudioPlayer.ENEMY_DEAD);

                enemies.remove(i);
                i--;

                if (enemy.type == Enemy.TYPE_NORMAL) achievements.blue++;
                if (enemy.type == Enemy.TYPE_FAST) achievements.green++;
                if (enemy.type == Enemy.TYPE_IMMUNE) achievements.yellow++;
                if (enemy.type == Enemy.TYPE_STRONG) achievements.pink++;

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

    private void updateSubtitles() {
        for (int i = 0; i < subtitles.size(); i++) {
            boolean remove = subtitles.get(i).update();
            if (remove) {
                subtitles.remove(i);
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
                scoreMultiplier = 1;
            }
        }
    }

    private void updateFly() {
        if (flyTimer != 0) {
            flyDiff = (System.nanoTime() - flyTimer) / 1000000;
            if (flyDiff > powerLength) {
                flyTimer = 0;
                flyTopY = 0;
                player.fly = false;
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
                    if (mode == MODE_HARDCORE) player.dead = true;
                    else player.loseLife();
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

                if (game.preferences.getSetting(ExcoreSharedPreferences.KEY_SUBS))
                    subtitles.add(new Subtitle(game, powerUp.text));

                if (powerUp.type == PowerUp.TYPE_LIFE) {
                    game.audioPlayer.playSound(AudioPlayer.POWER_UP_LIFE);
                    player.gainLife();
                    lucky = false;
                }
                if (powerUp.type == PowerUp.TYPE_POWER) {
                    game.audioPlayer.playSound(AudioPlayer.POWER_UP_POWER);
                    player.increasePower(1);
                    lucky = false;
                    updatesWeak = false;
                }

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
                    lucky = false;
                }

                if (powerUp.type == PowerUp.TYPE_DESTROY) {
                    game.audioPlayer.playSound(AudioPlayer.POWER_UP_DESTROY);
                    for (Enemy enemy : enemies)
                        enemy.destroy();
                    lucky = false;
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

                if (powerUp.type == PowerUp.TYPE_FLY) {
                    game.audioPlayer.playSound(AudioPlayer.POWER_UP_FLY);
                    flyTimer = System.nanoTime();
                    player.fly = true;
                    if (flyTopY == 0) flyTopY = getFreeTimerPosition();
                }

                if (powerUp.type == PowerUp.TYPE_IMMORTALITY) {
                    game.audioPlayer.playSound(AudioPlayer.POWER_UP_IMMORTAL);
                    immortalTimer = System.nanoTime();
                    player.immortal = true;
                    if (immortalTopY == 0) immortalTopY = getFreeTimerPosition();
                    lucky = false;
                }

                if (powerUp.type == PowerUp.TYPE_DOUBLE_SCORE) {
                    game.audioPlayer.playSound(AudioPlayer.POWER_UP_SCORE);
                    scoreTimer = System.nanoTime();
                    scoreMultiplier *= 2; // Doubles the score multiplier every power up the player collects
                    if (scoreMultiplier > 64) scoreMultiplier = 64; // 64 will be max multiplier
                    if (scoreTopY == 0) scoreTopY = getFreeTimerPosition();
                    lucky = false;
                }

                powerUps.remove(i);
                i--;
            }
        }
    }

    private int getFreeTimerPosition() {
        int position = 40;
        if (slowTopY != position && fastTopY != position && immortalTopY != position && scoreTopY != position && flyTopY != position)
            return position;
        position = 60;
        if (slowTopY != position && fastTopY != position && immortalTopY != position && scoreTopY != position && flyTopY != position)
            return position;
        position = 80;
        if (slowTopY != position && fastTopY != position && immortalTopY != position && scoreTopY != position && flyTopY != position)
            return position;
        position = 100;
        if (slowTopY != position && fastTopY != position && immortalTopY != position && scoreTopY != position && flyTopY != position)
            return position;
        return 40;
    }

    @Override
    public void draw(Canvas canvas) {
        canvas.drawColor(background);
        if (showNextWaveAnimation && waveNumber != 1) nextWave.draw(canvas);

        // Draw wave number
        drawWaveNumber(canvas);
        if (mode != MODE_HARDCORE) drawPlayerLives(canvas);
        drawPlayerScore(canvas);

        drawTimer(canvas, slowTimer, slowDiff, Color.GREEN, slowTopY);
        drawTimer(canvas, fastTimer, fastDiff, Color.BLUE, fastTopY);
        drawTimer(canvas, immortalTimer, immortalDiff, Color.YELLOW, immortalTopY);
        drawTimer(canvas, scoreTimer, scoreDiff, Color.GRAY, scoreTopY);
        drawTimer(canvas, flyTimer, flyDiff, Color.BLACK, flyTopY);
        drawPower(canvas);
        drawMenuButton(canvas);
        if (mode == MODE_TIME_ATTACK) drawCountDownTimer(canvas);

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

        for (Subtitle subtitle : subtitles)
            subtitle.draw(canvas);

        if (pause) pauseState.draw(canvas);
        if (player.dead) continueState.draw(canvas);
        if (showExitAnim) exitAnim.draw(canvas);
    }

    private void drawWaveNumber(Canvas canvas) {
        if (waveStartTimer != 0) {
            game.paint.setTypeface(game.tf);
            game.paint.setTextSize(50);
            String s = "-   W A V E   " + waveNumber + "   -";
            if (waveNumber % 10 == 0) s = "-   B O S S   -";
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
            canvas.drawCircle(25 + (25 * i), 100, player.r / 2, game.paint);

            game.paint.setStyle(Paint.Style.STROKE);
            game.paint.setColor(Color.WHITE);
            game.paint.setStrokeWidth(2);
            canvas.drawCircle(25 + (25 * i), 100, player.r / 2, game.paint);
            game.resetPaint();
        }
    }

    private void drawPlayerScore(Canvas canvas) {
        game.paint.setTypeface(game.tf);
        game.paint.setTextSize(35);
        game.paint.setColor(Color.WHITE);
        canvas.drawText("Score x" + scoreMultiplier, 15, 35, game.paint);
        canvas.drawText("" + player.score, 15, 70, game.paint);
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

    private void drawCountDownTimer(Canvas canvas) {
        game.paint.setTypeface(game.tf);
        game.paint.setTextSize(60);
        game.paint.setTextAlign(Paint.Align.CENTER);

        long reverse = waveNumber % 10 == 0 ? (countdownTimerLength * 2) / 1000 : countdownTimerLength / 1000;
        long time = reverse - countdownDiff / 1000;
        if (time >= 10) {
            countdownTimerString = "" + time;
        } else {
            countdownTimerString = "0" + time;
        }

        canvas.drawText(countdownTimerString, game.width / 2, 120, game.paint);
        game.resetPaint();
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

        if (pauseTimer != 0) {
            game.paint.setStyle(Paint.Style.FILL);
            game.paint.setColor(Color.WHITE);
            canvas.drawRect(pauseButton.left, pauseButton.top, pauseButton.right, pauseButton.bottom, game.paint);
            game.resetPaint();
        }

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
        } else if (player.dead) {
            continueState.handleInput(x, y);
        } else if (pauseButton.contains((int) x, (int) y)) {
            game.preferences.setSetting(ExcoreSharedPreferences.KEY_MOVE, false);
            game.audioPlayer.playSound(AudioPlayer.SOUND_BUTTON);
            pauseTimer = System.nanoTime();
        } else {
            player.setDestination(x, y);
        }
    }

    public void addPowerUp(Enemy enemy) {
        PowerUp powerUp = PowerUp.getPowerUp(enemy, mode, game);
        if (powerUp != null) powerUps.add(powerUp);
    }

    public void addBullet(float angle, double x, double y) {
        bullets.add(new Bullet(game, angle, x, y));
    }

    public void addEnemy(Enemy enemy) {
        if (slowTimer != 0) enemy.slow = true;
        if (fastTimer != 0) enemy.fast = true;
        enemies.add(enemy);
    }

    private void checkAchievements() {
        if (waveNumber == 11) {
            if (mode == MODE_NORMAL) achievements.normalNewbie = true;
            if (mode == MODE_HARDCORE) {
                achievements.hardcoreNewbie = true;
                if (updatesWeak) achievements.updatesWeak = true;
            }
            if (mode == MODE_TIME_ATTACK) achievements.timeNewbie = true;
            if (lucky) achievements.lucky = true;
        }

        if (waveNumber == 21) {
            if (mode == MODE_NORMAL) achievements.normalPro = true;
            if (mode == MODE_HARDCORE) achievements.hardcorePro = true;
            if (mode == MODE_TIME_ATTACK) achievements.timePro = true;
        }

        if (waveNumber == 31) {
            if (mode == MODE_NORMAL) achievements.normalGod = true;
            if (mode == MODE_HARDCORE) achievements.hardcoreGod = true;
            if (mode == MODE_TIME_ATTACK) achievements.timeGod = true;
        }
    }

    private void generateWave() {
        enemies.clear();
        Random random = new Random();
        int wave = waveNumber % 10;
        if (waveNumber < 10) wave = waveNumber;
        double multiplier = waveNumber / 10 + 1;

        Log.d("Wave", "Multi: " + multiplier);

        if (wave == 0) {
            addEnemy(new Enemy(game, this, Enemy.TYPE_BOSS, 4, multiplier));
        } else if (wave >= 1 && wave <= 3) {
            for (int i = 0; i < maxTypeOneWave; i++) {
                addEnemy(new Enemy(game, this, random.nextInt(4) + 1, 1, multiplier));
            }
        } else if (wave >= 4 && wave <= 6) {
            for (int i = 0; i < maxTypeTwoWave; i++) {
                addEnemy(new Enemy(game, this, random.nextInt(4) + 1, 2, multiplier));
            }
        } else {
            for (int i = 0; i < maxTypeThreeWave; i++) {
                addEnemy(new Enemy(game, this, random.nextInt(4) + 1, 3, multiplier));
            }
        }
    }
}
