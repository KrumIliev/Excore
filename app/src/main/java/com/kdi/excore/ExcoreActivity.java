package com.kdi.excore;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.games.Games;
import com.google.android.gms.plus.Plus;
import com.google.example.games.basegameutils.BaseGameUtils;
import com.kdi.excore.game.Game;
import com.kdi.excore.game.GameListener;

public class ExcoreActivity extends Activity implements GameListener, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private Game game;
    private GoogleApiClient mGoogleApiClient;

    private static int RC_SIGN_IN = 9001;
    private boolean mResolvingConnectionFailure = false;
    private boolean mAutoStartSignInflow = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Games.API).addScope(Games.SCOPE_GAMES)
                .addApi(Plus.API).addScope(Plus.SCOPE_PLUS_LOGIN)
                .build();

        game = new Game(this, this);
        setContentView(game);
    }

    @Override
    public void addToLeaderboard(String leaderboardID, int score) {
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected())
            Games.Leaderboards.submitScore(mGoogleApiClient, leaderboardID, score);
    }

    @Override
    public void openLeaderboard(String leaderboardID) {
        if (mGoogleApiClient != null) {
            if (mGoogleApiClient.isConnected()) {
                startActivityForResult(Games.Leaderboards.getLeaderboardIntent(mGoogleApiClient, leaderboardID), 1919);
            } else if (mGoogleApiClient.isConnecting()) {
                Toast.makeText(this, "Connecting ... ", Toast.LENGTH_LONG).show();
            }
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
        if (mGoogleApiClient != null) {
            if (mGoogleApiClient.isConnected()) {
                startActivityForResult(Games.Achievements.getAchievementsIntent(mGoogleApiClient), 2121);
            } else if (mGoogleApiClient.isConnecting()) {
                Toast.makeText(this, "Connecting ... ", Toast.LENGTH_LONG).show();
            }
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
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mGoogleApiClient.disconnect();
        game.googleIsConected = false;
    }

    @Override
    public void onConnected(Bundle bundle) {
        game.googleIsConected = true;
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        if (mResolvingConnectionFailure) return;
        if (mAutoStartSignInflow) {
            mAutoStartSignInflow = false;
            mResolvingConnectionFailure = true;

            if (!BaseGameUtils.resolveConnectionFailure(this, mGoogleApiClient,
                    connectionResult, RC_SIGN_IN, getString(R.string.google_failure))) {
                mResolvingConnectionFailure = false;
            }
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
    }

    @Override
    protected void onActivityResult(int request, int response, Intent data) {
        if (request == RC_SIGN_IN) {
            mResolvingConnectionFailure = false;
            if (response == RESULT_OK)
                mGoogleApiClient.connect();
            else
                BaseGameUtils.showActivityResultError(this, request, response, R.string.google_error);
        } else {
            super.onActivityResult(request, response, data);
        }
    }
}
