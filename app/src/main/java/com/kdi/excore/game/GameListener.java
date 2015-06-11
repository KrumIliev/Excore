package com.kdi.excore.game;

/**
 * Created by Krum Iliev on 5/29/2015.
 */
public interface GameListener {

    void addToLeaderboard(String leaderboardID, int score);

    void openLeaderboard(String leaderboardID);

    void unlockAchievement(String achievementID);

    void incrementAchievement(String achievementID, int value);

    void openAchievements();
}
