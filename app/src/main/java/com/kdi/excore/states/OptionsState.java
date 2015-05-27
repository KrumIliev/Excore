package com.kdi.excore.states;

import android.graphics.Canvas;
import android.graphics.Rect;

import com.kdi.excore.entities.Enemy;
import com.kdi.excore.game.Game;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by Krum Iliev on 5/27/2015.
 */
public class OptionsState extends State {

    private Rect musicButton;
    private Rect soundButton;
    private Rect subsButton;

    private String musicON, musicOFF;
    private String soundON, soundOFF;
    private String subsON, subsOFF, subsWTF;

    public ArrayList<Enemy> objects;

    public OptionsState(StateManager stateManager, Game game, int color) {
        super(stateManager, game);

        musicON = "- M U S I C     O N -";
        musicOFF = "- M U S I C     O F F -";
        soundON = "- S O U N D     O N -";
        soundOFF = "- S O U N D     O F F -";
        subsON = " - S U B T I T L E S     O N -";
        subsOFF = " - S U B T I T L E S     O F F -";
        subsWTF = "Lol wut?";


    }

    @Override
    public void handleInput(float x, float y) {

    }

    @Override
    public void update() {
        for (Enemy enemy : objects)
            enemy.update();
    }

    @Override
    public void draw(Canvas canvas) {

    }

    private void initObjects() {
        Random random = new Random();
        objects = new ArrayList<>();
        objects.add(new Enemy(game, this, 2, 1, random.nextInt(game.width), random.nextInt(game.height)));
        objects.add(new Enemy(game, this, 2, 1, random.nextInt(game.width), random.nextInt(game.height)));
        objects.add(new Enemy(game, this, 2, 1, random.nextInt(game.width), random.nextInt(game.height)));
        objects.add(new Enemy(game, this, 2, 1, random.nextInt(game.width), random.nextInt(game.height)));
        objects.add(new Enemy(game, this, 2, 1, random.nextInt(game.width), random.nextInt(game.height)));
        objects.add(new Enemy(game, this, 2, 1, random.nextInt(game.width), random.nextInt(game.height)));
        objects.add(new Enemy(game, this, 2, 1, random.nextInt(game.width), random.nextInt(game.height)));
        objects.add(new Enemy(game, this, 2, 1, random.nextInt(game.width), random.nextInt(game.height)));
        objects.add(new Enemy(game, this, 2, 1, random.nextInt(game.width), random.nextInt(game.height)));
    }
}
