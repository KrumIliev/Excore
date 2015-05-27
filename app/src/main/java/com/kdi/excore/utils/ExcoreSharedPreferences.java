package com.kdi.excore.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Krum Iliev on 5/27/2015.
 */
public class ExcoreSharedPreferences {

    private final String PREFERENCE_FILE = "excore_preference";

    public static final String KEY_MUSIC = "music";
    public static final String KEY_SOUND = "sound";
    public static final String KEY_SUBS = "subs";

    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;

    public ExcoreSharedPreferences(Context context) {
        preferences = context.getSharedPreferences(PREFERENCE_FILE, Context.MODE_PRIVATE);
        editor = preferences.edit();
    }

    public void setSetting(String key, boolean state) {
        editor.putBoolean(key, state).commit();
    }

    public boolean getSetting(String key) {
        return preferences.getBoolean(key, true);
    }

}
