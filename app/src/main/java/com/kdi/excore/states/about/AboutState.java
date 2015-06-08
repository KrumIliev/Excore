package com.kdi.excore.states.about;

import android.graphics.Canvas;

import com.kdi.excore.entities.Enemy;
import com.kdi.excore.game.Game;
import com.kdi.excore.states.StateManager;
import com.kdi.excore.states.menu.MainMenuState;
import com.kdi.excore.states.menu.Menu;

import java.util.ArrayList;

/**
 * Created by Krum Iliev on 5/30/2015.
 */
public class AboutState extends Menu {

    private ArrayList<AboutText> texts;
    private ArrayList<AboutText> aboutTexts;

    private int index;

    private long timer;
    private long diff;
    private long delay;
    private boolean nextText;
    private boolean initial;

    public AboutState(StateManager stateManager, Game game, int color) {
        super(stateManager, game, color, Enemy.TYPE_FAST);
        texts = new ArrayList<>();
        initStrings();
        index = -1;
        delay = 200;
        nextState = new MainMenuState(stateManager, game, anim.color);
        initial = true;
    }

    private void initStrings() {
        aboutTexts = new ArrayList<>();
        aboutTexts.add(new AboutText(game, "- D E V E L O P E R -", "K R U M   I L I E V"));
        aboutTexts.add(new AboutText(game, "- M U S I C -", null));
        aboutTexts.add(new AboutText(game, "A   H i m i t s u", "C e a s e"));
        aboutTexts.add(new AboutText(game, "D a P l a q u e", "D r e a m"));
        aboutTexts.add(new AboutText(game, "D i g i t a l    M a t h", "I n f i n i t e   C o s m o s"));
        aboutTexts.add(new AboutText(game, "D o c t o r   V o x", "F r o n t i e r"));
        aboutTexts.add(new AboutText(game, "D o c t o r   V o x", "H e r o"));
        aboutTexts.add(new AboutText(game, "D o c t o r   V o x", "L e v e l   U p"));
        aboutTexts.add(new AboutText(game, "D y l a n   H a r d y", "K i t e s"));
        aboutTexts.add(new AboutText(game, "F r o m   T h e   D u s t", "A l i v e"));
        aboutTexts.add(new AboutText(game, "F r o m   T h e   D u s t", "B r e a t h"));
        aboutTexts.add(new AboutText(game, "F r o m   T h e   D u s t", "S t a r d u s t"));
        aboutTexts.add(new AboutText(game, "F r o m   T h e   D u s t", "S u p e r n o v a"));
        aboutTexts.add(new AboutText(game, "M a d n a p", "E n d l e s s   R i v e r"));
        aboutTexts.add(new AboutText(game, "T h o m a s   V X", "S t r a n g e r"));
    }

    @Override
    public void handleInput(float x, float y) {
        if (showAnim) return;
        showAnim = true;
    }

    @Override
    public void update() {
        super.update();

        for (int i = 0; i < texts.size(); i++) {
            boolean remove = texts.get(i).update();
            if (remove) {
                texts.remove(i);
                i--;
                if (texts.size() == 0) showAnim = true;
            }
        }

        if (timer == 0) {
            index++;
            nextText = false;
            timer = System.nanoTime();
        } else {
            diff = (System.nanoTime() - timer) / 1000000;
            if (diff > delay) {
                nextText = true;
                timer = 0;
                diff = 0;
                if (initial) {
                    delay = 1500;
                    initial = false;
                }
            }
        }

        if (nextText && index < aboutTexts.size()) texts.add(aboutTexts.get(index));
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);

        for (AboutText text : texts)
            text.draw(canvas);

        if (showAnim) anim.draw(canvas);
    }
}
