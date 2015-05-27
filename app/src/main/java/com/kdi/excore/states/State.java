package com.kdi.excore.states;

import android.graphics.Canvas;

import com.kdi.excore.game.Game;

/**
 * Created by Krum Iliev on 5/25/2015.
 */
public abstract class State {

    protected StateManager stateManager;
    protected Game game;
    protected int background;

    public State(StateManager stateManager, Game game) {
        this.stateManager = stateManager;
        this.game = game;
    }

    public abstract void handleInput (float x, float y);

    public abstract void update();

    public abstract void draw(Canvas canvas);
}
