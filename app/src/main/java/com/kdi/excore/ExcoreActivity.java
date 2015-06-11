package com.kdi.excore;

import android.os.Bundle;
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
    }

    @Override
    public void onSignInFailed() {}

    @Override
    public void onSignInSucceeded() {
        mGoogleApiClient = getApiClient();
    }

    @Override
    public void addToLeaderboard(String leaderboardID, int score) {
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected())
            Games.Leaderboards.submitScore(mGoogleApiClient, leaderboardID, score);
    }

    @Override
    public void openLeaderboard(String leaderboardID) {
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            startActivityForResult(Games.Leaderboards.getLeaderboardIntent(mGoogleApiClient, leaderboardID), 1919);
        } else if (getApiClient().isConnecting()) {
            Toast.makeText(this, "Connecting ... ", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "Google play not connected", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void unlockAchievement(String achievementID) {
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected())
            Games.Achievements.unlock(mGoogleApiClient, achievementID);
    }

    @Override
    public void incrementAchievement(String achievementID, int value) {
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected())
            Games.Achievements.increment(mGoogleApiClient, achievementID, value);
    }

    @Override
    public void openAchievements() {
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            startActivityForResult(Games.Achievements.getAchievementsIntent(mGoogleApiClient), 2121);
        } else if (getApiClient().isConnecting()) {
            Toast.makeText(this, "Connecting ... ", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "Google play not connected", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        game.paused = false;
        if (getApiClient().isConnected()) mGoogleApiClient = getApiClient();
    }

    @Override
    protected void onPause() {
        super.onPause();
        game.paused = true;
    }

    @Override
    public void onConnected(Bundle bundle) {}

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {}

    @Override
    public void onConnectionSuspended(int i) {}
}
