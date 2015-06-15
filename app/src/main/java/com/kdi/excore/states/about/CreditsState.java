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
public class CreditsState extends Menu {

    private ArrayList<CreditsText> visibleCredits;
    private ArrayList<CreditsText> allCredits;

    private int index;

    private long timer;
    private long diff;
    private long delay;
    private boolean nextText;
    private boolean initial;

    public CreditsState(StateManager stateManager, Game game, int color) {
        super(stateManager, game, color, Enemy.TYPE_FAST);
        visibleCredits = new ArrayList<>();
        initStrings();
        index = -1;
        delay = 200;
        nextState = new MainMenuState(stateManager, game, anim.color);
        initial = true;
    }

    private void initStrings() {
        allCredits = new ArrayList<>();
        allCredits.add(new CreditsText(game, "- D E V E L O P E R -", "K R U M   I L I E V"));
        allCredits.add(new CreditsText(game, "- O R I G I N A L   I D E A -", "F o r e i g n G u y M i k e"));
        allCredits.add(new CreditsText(game, "- M U S I C -", null));
        allCredits.add(new CreditsText(game, "A   H i m i t s u", "C e a s e"));
        allCredits.add(new CreditsText(game, "D a P l a q u e", "D r e a m"));
        allCredits.add(new CreditsText(game, "D i g i t a l    M a t h", "I n f i n i t e   C o s m o s"));
        allCredits.add(new CreditsText(game, "D o c t o r   V o x", "F r o n t i e r"));
        allCredits.add(new CreditsText(game, "D o c t o r   V o x", "H e r o"));
        allCredits.add(new CreditsText(game, "D o c t o r   V o x", "L e v e l   U p"));
        allCredits.add(new CreditsText(game, "D y l a n   H a r d y", "K i t e s"));
        allCredits.add(new CreditsText(game, "F r o m   T h e   D u s t", "A l i v e"));
        allCredits.add(new CreditsText(game, "F r o m   T h e   D u s t", "B r e a t h"));
        allCredits.add(new CreditsText(game, "F r o m   T h e   D u s t", "S t a r d u s t"));
        allCredits.add(new CreditsText(game, "F r o m   T h e   D u s t", "S u p e r n o v a"));
        allCredits.add(new CreditsText(game, "I n a k i", "P h o e n i x"));
        allCredits.add(new CreditsText(game, "M a d n a p", "E n d l e s s   R i v e r"));
        allCredits.add(new CreditsText(game, "S y n x  X  P a r a n o r M e o w", "F a l l i n g"));
        allCredits.add(new CreditsText(game, "T h o m a s   V X", "S t r a n g e r"));
    }

    @Override
    public void handleInput(float x, float y) {
        if (showAnim) return;
        showAnim = true;
    }

    @Override
    public void update() {
        super.update();

        for (int i = 0; i < visibleCredits.size(); i++) {
            boolean remove = visibleCredits.get(i).update();
            if (remove) {
                visibleCredits.remove(i);
                i--;
                if (visibleCredits.size() == 0) showAnim = true;
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

        if (nextText && index < allCredits.size()) visibleCredits.add(allCredits.get(index));
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);

        for (CreditsText text : visibleCredits)
            text.draw(canvas);

        if (showAnim) anim.draw(canvas);
    }
}
