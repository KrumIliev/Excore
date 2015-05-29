package com.kdi.excore;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import com.kdi.excore.game.Game;
import com.kdi.excore.game.GameListener;

public class ExcoreActivity extends Activity implements GameListener {

    private Game game;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        game = new Game(this, this);
        setContentView(game);
    }

    @Override
    protected void onResume() {
        super.onResume();
        game.paused = false;
    }

    @Override
    protected void onPause() {
        super.onPause();
        game.paused = true;
    }

    @Override
    public void onExit() {
        finish();
    }
}
