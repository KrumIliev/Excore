package com.kdi.excore.game;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.kdi.excore.R;
import com.kdi.excore.entities.Bullet;
import com.kdi.excore.entities.Enemy;
import com.kdi.excore.entities.Player;
import com.kdi.excore.entities.PowerUp;
import com.kdi.excore.xfx.AudioPlayer;
import com.kdi.excore.xfx.Explosion;
import com.kdi.excore.xfx.NextWaveAnimation;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by Krum Iliev on 5/22/2015.
 */
public class Game extends SurfaceView implements SurfaceHolder.Callback {

    private static final String LOG_TAG = Game.class.getSimpleName();
    private GameThread thread;

    private Player player;
    private ArrayList<Bullet> bullets;
    private ArrayList<Enemy> enemies;
    private ArrayList<Explosion> explosions;
    private ArrayList<PowerUp> powerUps;

    private boolean showNextWaveAnimation;
    private NextWaveAnimation nextWave;

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

    private Typeface tf;

    private int background;
    private AudioPlayer audioPlayer;

    public Game(Context context) {
        super(context);
        getHolder().addCallback(this);
        thread = new GameThread(this);
        setFocusable(true);

        waveStartTimer = 0;
        waveStartTimerDiff = 0;
        waveStart = true;
        waveNumber = 0;

        showNextWaveAnimation = false;
        background = getRandomColor();

        tf = Typeface.createFromAsset(getContext().getAssets(), "font.ttf");
        audioPlayer = new AudioPlayer(getContext());
        audioPlayer.playMusic(R.raw.track_1);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        player = new Player(this);
        bullets = new ArrayList<>();
        enemies = new ArrayList<>();
        explosions = new ArrayList<>();
        powerUps = new ArrayList<>();
        nextWave = new NextWaveAnimation(this, getRandomColor());

        thread.setRunning(true);
        thread.start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        Log.d(LOG_TAG, "Surface is being destroyed");
        boolean retry = true;

        audioPlayer.stopMusic();

        thread.setRunning(false);
        while (retry) {
            try {
                thread.join();
                retry = false;
            } catch (InterruptedException e) {
                // try again shutting down the thread
            }
        }
        Log.d(LOG_TAG, "Thread was shut down cleanly");
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            player.setDestination(event.getX(), event.getY());
        }
        return super.onTouchEvent(event);
    }

    public void update() {
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
                background = nextWave.getColor();
                nextWave.reset(getRandomColor());
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
            if (enemy.isDead()) {
                addPowerUp(enemy);

                player.addScore(enemy.getType() + enemy.getRank());
                enemies.remove(i);
                i--;

                enemy.explode();
                explosions.add(new Explosion((float) enemy.getX(), (float) enemy.getY(), (int) enemy.getR(), (int) enemy.getR() + 30));
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
                    enemy.setSlow(false);
            }
        }
    }

    private void updateFastEnemies() {
        if (fastTimer != 0) {
            fastTimerDiff = (System.nanoTime() - fastTimer) / 1000000;
            if (fastTimerDiff > fastLength) {
                fastTimer = 0;
                for (Enemy enemy : enemies)
                    enemy.setFast(false);
            }
        }
    }

    private void checkBulletEnemyCollision() {
        for (int i = 0; i < bullets.size(); i++) {
            Bullet bullet = bullets.get(i);
            for (int j = 0; j < enemies.size(); j++) {
                Enemy enemy = enemies.get(j);

                double dx = bullet.getX() - enemy.getX();
                double dy = bullet.getY() - enemy.getY();
                double dist = Math.sqrt(dx * dx + dy * dy);

                if (dist < bullet.getR() + enemy.getR()) {
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
                double dx = player.getX() - enemy.getX();
                double dy = player.getY() - enemy.getY();
                double dist = Math.sqrt(dx * dx + dy * dy);
                if (dist < player.getR() + enemy.getR()) {
                    player.loseLife();
                }
            }
        }
    }

    private void checkPlayerPowerUpCollision() {
        for (int i = 0; i < powerUps.size(); i++) {
            PowerUp powerUp = powerUps.get(i);
            double dx = player.getX() - powerUp.getX();
            double dy = player.getY() - powerUp.getY();
            double dist = Math.sqrt(dx * dx + dy * dy);

            if (dist < player.getR() + powerUp.getR()) {

                if (powerUp.getType() == PowerUp.TYPE_LIFE) player.gainLife();
                if (powerUp.getType() == PowerUp.TYPE_POWER) player.increasePower(1);

                if (powerUp.getType() == PowerUp.TYPE_SLOW) {
                    slowDownTimer = System.nanoTime();
                    fastTimer = 0;
                    for (Enemy enemy : enemies) {
                        enemy.setFast(false);
                        enemy.setSlow(true);
                    }

                }

                if (powerUp.getType() == PowerUp.TYPE_DESTROY) {
                    for (Enemy enemy : enemies)
                        enemy.destroy();
                    //TODO set screen to flash red
                }

                if (powerUp.getType() == PowerUp.TYPE_FASTER_ENEMY) {
                    fastTimer = System.nanoTime();
                    slowDownTimer = 0;
                    for (Enemy enemy : enemies) {
                        enemy.setSlow(false);
                        enemy.setFast(true);
                    }
                }

                if (powerUp.getType() == PowerUp.TYPE_UPDATE_ENEMY) {
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
        if (canvas == null) return;
        canvas.drawColor(background);

        Paint paint = new Paint();
        paint.setAntiAlias(true);

        if (showNextWaveAnimation && waveNumber != 1) nextWave.draw(canvas);

        // Draw wave number
        drawWaveNumber(canvas);
        drawPlayerLives(canvas);
        drawPlayerScore(canvas);
        drawSlowTimer(canvas);
        drawFastTimer(canvas);
        drawPower(canvas);


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

    public void addBullet(float angle, double x, double y) {
        bullets.add(new Bullet(this, angle, x, y));
    }

    public void addEnemy(Enemy enemy) {
        enemies.add(enemy);
    }

    public void playSound(int sound) {
        audioPlayer.playSound(sound);
    }

    public void addPowerUp(Enemy enemy) {
        double rand = Math.random();
        if (rand < 0.001)
            powerUps.add(new PowerUp(this, PowerUp.TYPE_LIFE, enemy.getX(), enemy.getY()));
        else if (rand < 0.020)
            powerUps.add(new PowerUp(this, PowerUp.TYPE_DESTROY, enemy.getX(), enemy.getY()));
        else if (rand < 0.100)
            powerUps.add(new PowerUp(this, PowerUp.TYPE_FASTER_ENEMY, enemy.getX(), enemy.getY()));
        else if (rand < 0.120)
            powerUps.add(new PowerUp(this, PowerUp.TYPE_POWER, enemy.getX(), enemy.getY()));
        else if (rand < 0.130)
            powerUps.add(new PowerUp(this, PowerUp.TYPE_SLOW, enemy.getX(), enemy.getY()));
        else if (rand < 0.140)
            powerUps.add(new PowerUp(this, PowerUp.TYPE_UPDATE_ENEMY, enemy.getX(), enemy.getY()));

    }

    private void drawWaveNumber(Canvas canvas) {
        Paint paint = new Paint();
        paint.setAntiAlias(true);

        if (waveStartTimer != 0) {
            paint.setTypeface(tf);
            paint.setTextSize(50);
            String s = "-   W A V E   " + waveNumber + "   -";
            float length = paint.measureText(s);
            int alpha = (int) (255 * Math.sin(3.14 * waveStartTimerDiff / waveDelay));
            if (alpha > 255) alpha = 255;
            paint.setColor(Color.argb(alpha, 255, 255, 255));
            canvas.drawText(s, getWidth() / 2 - length / 2, getHeight() / 2, paint);
        }
    }

    private void drawPlayerLives(Canvas canvas) {
        Paint paint = new Paint();
        paint.setAntiAlias(true);

        for (int i = 0; i < player.getLives(); i++) {
            paint.setStyle(Paint.Style.FILL);
            paint.setColor(Color.GRAY);
            canvas.drawCircle(25 + (25 * i), 70, player.getR() / 2, paint);

            paint.setStyle(Paint.Style.STROKE);
            paint.setColor(Color.WHITE);
            paint.setStrokeWidth(2);
            canvas.drawCircle(25 + (25 * i), 70, player.getR() / 2, paint);
        }
    }

    private void drawPlayerScore(Canvas canvas) {
        Paint paint = new Paint();
        paint.setAntiAlias(true);

        paint.setTypeface(tf);
        paint.setTextSize(35);
        paint.setColor(Color.WHITE);
        canvas.drawText("Score: " + player.getScore(), 15, 35, paint);
    }

    private void drawSlowTimer(Canvas canvas) {
        if (slowDownTimer != 0) {

            Paint paint = new Paint();
            paint.setAntiAlias(true);

            paint.setStyle(Paint.Style.FILL);
            paint.setColor(Color.GREEN);
            canvas.drawRect(getWidth() - 150, 50, (float) ((getWidth() - 50) - 100.0 * slowTimerDiff / slowDownLength), 60, paint);

            paint.setStyle(Paint.Style.STROKE);
            paint.setColor(Color.WHITE);
            paint.setStrokeWidth(2);
            canvas.drawRect(getWidth() - 150, 50, getWidth() - 50, 60, paint);
        }
    }

    private void drawFastTimer(Canvas canvas) {
        if (fastTimer != 0) {

            Paint paint = new Paint();
            paint.setAntiAlias(true);

            paint.setStyle(Paint.Style.FILL);
            paint.setColor(Color.BLUE);
            canvas.drawRect(getWidth() - 150, 50, (float) ((getWidth() - 50) - 100.0 * fastTimerDiff / fastLength), 60, paint);

            paint.setStyle(Paint.Style.STROKE);
            paint.setColor(Color.WHITE);
            paint.setStrokeWidth(2);
            canvas.drawRect(getWidth() - 150, 50, getWidth() - 50, 60, paint);
        }
    }

    private void drawPower(Canvas canvas) {
        Paint paint = new Paint();
        paint.setAntiAlias(true);

        paint.setStyle(Paint.Style.FILL);
        int color = Color.WHITE;
        if (player.getPowerLevel() == 1) color = Color.GREEN;
        if (player.getPowerLevel() == 2) color = Color.BLUE;
        if (player.getPowerLevel() == 3) color = Color.YELLOW;
        if (player.getPowerLevel() == 4) color = Color.RED;
        paint.setColor(color);
        if (player.getPowerLevel() == 4) {
            canvas.drawRect(getWidth() - 150, 30, getWidth() - 50, 40, paint);
        } else {
            canvas.drawRect(getWidth() - 150, 30, (float) (getWidth() - 150 + 100.0 * player.getPower() / player.getRequiredPower()), 40, paint);
        }


        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.WHITE);
        paint.setStrokeWidth(2);
        canvas.drawRect(getWidth() - 150, 30, getWidth() - 50, 40, paint);
    }

    private int getRandomColor() {
        Random rnd = new Random();
        return Color.argb(255, rnd.nextInt(150), rnd.nextInt(150), rnd.nextInt(150));
    }

    private void createNewEnemies() {
        enemies.clear();

        if (waveNumber == 1) {
            for (int i = 0; i < 4; i++) {
                enemies.add(new Enemy(this, 1, 1));
            }
        }

        if (waveNumber == 2) {
            for (int i = 0; i < 4; i++) {
                enemies.add(new Enemy(this, 1, 1));
            }
            enemies.add(new Enemy(this, 1, 2));
            enemies.add(new Enemy(this, 1, 2));
        }

        if (waveNumber == 3) {
            enemies.add(new Enemy(this, 1, 3));
            enemies.add(new Enemy(this, 1, 3));
            enemies.add(new Enemy(this, 1, 4));
        }

        if (waveNumber == 4) {
            for (int i = 0; i < 4; i++) {
                enemies.add(new Enemy(this, 2, 1));
            }
        }

        if (waveNumber == 5) {
            for (int i = 0; i < 4; i++) {
                enemies.add(new Enemy(this, 2, 1));
            }
            enemies.add(new Enemy(this, 2, 2));
            enemies.add(new Enemy(this, 2, 2));
        }

        if (waveNumber == 6) {
            enemies.add(new Enemy(this, 2, 3));
            enemies.add(new Enemy(this, 2, 3));
            enemies.add(new Enemy(this, 2, 4));
        }

        if (waveNumber == 7) {
            for (int i = 0; i < 4; i++) {
                enemies.add(new Enemy(this, 3, 1));
            }
        }

        if (waveNumber == 8) {
            for (int i = 0; i < 4; i++) {
                enemies.add(new Enemy(this, 3, 1));
            }
            enemies.add(new Enemy(this, 3, 2));
            enemies.add(new Enemy(this, 3, 2));
        }

        if (waveNumber == 9) {
            enemies.add(new Enemy(this, 3, 3));
            enemies.add(new Enemy(this, 3, 3));
            enemies.add(new Enemy(this, 3, 4));
        }
    }
}
