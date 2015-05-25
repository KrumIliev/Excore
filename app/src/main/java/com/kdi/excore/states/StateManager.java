package com.kdi.excore.states;

import android.graphics.Canvas;

import java.util.Stack;

/**
 * Created by Krum Iliev on 5/25/2015.
 */
public class StateManager {

    private Stack<State> states;

    public StateManager() {
        states = new Stack<>();
    }

    public void push(State state) {
        states.push(state);
    }

    public void pop() {
        states.pop();
    }

    public void setState(State state) {
        states.pop();
        states.push(state);
    }

    public void update() {
        states.peek().update();
    }

    public void draw(Canvas canvas) {
        states.peek().draw(canvas);
    }

    public void handleInput(float x, float y) {
        states.peek().handleInput(x, y);
    }
}
