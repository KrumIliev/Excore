package com.kdi.excore;

import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.games.Games;
import com.google.example.games.basegameutils.BaseGameActivity;
import com.kdi.excore.game.Game;
import com.kdi.excore.game.GameListener;

public class ExcoreActivity extends BaseGameActivity implements GameListener, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private Game game;
    private GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setRequestedClients(CLIENT_GAMES | CLIENT_PLUS);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        game = new Game(this, this);
        setContentView(game);
        beginUserInitiatedSignIn();
    }

    @Override
    public void onSignInFailed() {
        Log.d("Game", "onSignInFailed()");
    }

    @Override
    public void onSignInSucceeded() {
        mGoogleApiClient = getApiClient();
        Log.d("Game", "onSignInSucceeded()");
    }

    @Override
    public void addToLeaderboard(String leaderboardID, int score) {
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            Games.Leaderboards.submitScore(mGoogleApiClient, leaderboardID, score);
        } else {
            Toast.makeText(this, "Google play not connected", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void openLeaderboard(String leaderboardID) {
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            startActivityForResult(Games.Leaderboards.getLeaderboardIntent(mGoogleApiClient, leaderboardID), 1919);
        } else {
            Toast.makeText(this, "Google play not connected", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void unlockAchievement(String achievementID) {
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            Games.Achievements.unlock(mGoogleApiClient, achievementID);
        } else {
            Toast.makeText(this, "Google play not connected", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void incrementAchievement(String achievementID, int value) {
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            Games.Achievements.increment(mGoogleApiClient, achievementID, value);
            Log.d("Game", "Incrementing: " + achievementID + " With: " + value);
        } else {
            Toast.makeText(this, "Google play not connected", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void openAchievements() {
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            startActivityForResult(Games.Achievements.getAchievementsIntent(mGoogleApiClient), 2121);
        } else {
            Toast.makeText(this, "Google play not connected", Toast.LENGTH_LONG).show();
        }
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

    @Override
    public void onConnected(Bundle bundle) {
        Log.d("Game", "Google Connected");
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.d("Game", "Google Failed" + connectionResult.toString());
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d("Game", "Google onConnectionSuspended");
    }
}
