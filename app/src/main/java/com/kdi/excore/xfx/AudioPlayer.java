package com.kdi.excore.xfx;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Build;

import com.kdi.excore.R;
import com.kdi.excore.utils.ExcoreSharedPreferences;

import java.util.Random;

/**
 * Created by Krum Iliev on 5/24/2015.
 */
public class AudioPlayer {

    public static final int SOUND_WEAPON_0 = 0;
    public static final int SOUND_WEAPON_1 = 1;
    public static final int SOUND_WEAPON_2 = 2;
    public static final int SOUND_WEAPON_3 = 3;
    public static final int SOUND_WEAPON_4 = 4;
    public static final int POWER_UP_DESTROY = 5;
    public static final int POWER_UP_SLOW = 6;
    public static final int POWER_UP_FAST = 7;
    public static final int POWER_UP_FLY = 8;
    public static final int POWER_UP_IMMORTAL = 9;
    public static final int POWER_UP_LIFE = 10;
    public static final int ENEMY_DEAD = 11;
    public static final int SOUND_BUTTON = 12;
    public static final int POWER_UP_POWER = 13;
    public static final int POWER_UP_SCORE = 14;
    public static final int SOUND_WEAPON_DISCHARGE = 15;


    private Context context;
    private MediaPlayer mediaPlayer; // for playing music

    private SoundPool soundPool; // for playing short audio files like effects
    private int soundIds[];
    private int musicIds[];
    private int currentIndex = 0;

    private ExcoreSharedPreferences preferences;

    public AudioPlayer(Context context) {
        this.context = context;
        this.preferences = new ExcoreSharedPreferences(context);

        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.LOLLIPOP) {
            SoundPool.Builder poolBuilder = new SoundPool.Builder();
            poolBuilder.setMaxStreams(16);
            AudioAttributes.Builder audioAttributesBuilder = new AudioAttributes.Builder();
            audioAttributesBuilder.setUsage(AudioAttributes.USAGE_GAME);
            audioAttributesBuilder.setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION);
            poolBuilder.setAudioAttributes(audioAttributesBuilder.build());
            soundPool = poolBuilder.build();
        } else {
            soundPool = new SoundPool(16, AudioManager.STREAM_MUSIC, 0);
        }

        soundIds = new int[16];
        soundIds[SOUND_WEAPON_0] = soundPool.load(context, R.raw.shoot_0, 1);
        soundIds[SOUND_WEAPON_1] = soundPool.load(context, R.raw.shoot_1, 1);
        soundIds[SOUND_WEAPON_2] = soundPool.load(context, R.raw.shoot_2, 1);
        soundIds[SOUND_WEAPON_3] = soundPool.load(context, R.raw.shoot_3, 1);
        soundIds[SOUND_WEAPON_4] = soundPool.load(context, R.raw.shoot_4, 1);
        soundIds[POWER_UP_DESTROY] = soundPool.load(context, R.raw.p_destroy, 1);
        soundIds[POWER_UP_SLOW] = soundPool.load(context, R.raw.p_slow, 1);
        soundIds[POWER_UP_FAST] = soundPool.load(context, R.raw.p_fast, 1);
        soundIds[POWER_UP_FLY] = soundPool.load(context, R.raw.p_fly, 1);
        soundIds[POWER_UP_IMMORTAL] = soundPool.load(context, R.raw.p_immortal, 1);
        soundIds[POWER_UP_LIFE] = soundPool.load(context, R.raw.p_life, 1);
        soundIds[POWER_UP_POWER] = soundPool.load(context, R.raw.p_power, 1);
        soundIds[SOUND_BUTTON] = soundPool.load(context, R.raw.button, 1);
        soundIds[ENEMY_DEAD] = soundPool.load(context, R.raw.enemy_dead, 1);
        soundIds[POWER_UP_SCORE] = soundPool.load(context, R.raw.p_score, 1);
        soundIds[SOUND_WEAPON_DISCHARGE] = soundPool.load(context, R.raw.discharge, 1);

        musicIds = new int[14];
        musicIds[0] = R.raw.track_1;
        musicIds[1] = R.raw.track_3;
        musicIds[2] = R.raw.track_4;
        musicIds[3] = R.raw.track_5;
        musicIds[4] = R.raw.track_6;
        musicIds[5] = R.raw.track_7;
        musicIds[6] = R.raw.track_8;
        musicIds[7] = R.raw.track_9;
        musicIds[8] = R.raw.track_10;
        musicIds[9] = R.raw.track_11;
        musicIds[10] = R.raw.track_12;
        musicIds[11] = R.raw.track_13;
        musicIds[12] = R.raw.track_14;
        musicIds[13] = R.raw.track_2;
        shuffleMusic(musicIds);
    }

    public void playMusic() {
        if (preferences.getSetting(ExcoreSharedPreferences.KEY_MUSIC)) {
            if (mediaPlayer != null) mediaPlayer.release();
            if (currentIndex > 13) currentIndex = 0;
            mediaPlayer = MediaPlayer.create(context, musicIds[currentIndex]);
            mediaPlayer.setLooping(false);
            mediaPlayer.setVolume(50, 50);

            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    currentIndex++;
                    playMusic();
                }
            });

            mediaPlayer.start();
        }
    }

    public void stopMusic() {
        if (mediaPlayer != null) mediaPlayer.release();
    }

    public void playSound(int sound) {
        if (preferences.getSetting(ExcoreSharedPreferences.KEY_SOUND))
            soundPool.play(soundIds[sound], 1, 1, 1, 0, 1f);
    }

    public void dispose() {
        if (mediaPlayer != null) mediaPlayer.release();
        if (soundPool != null) soundPool.release();
    }

    private void shuffleMusic(int[] ar) {
        Random rnd = new Random();
        for (int i = ar.length - 1; i > 0; i--) {
            int index = rnd.nextInt(i + 1);
            int a = ar[index];
            ar[index] = ar[i];
            ar[i] = a;
        }
    }
}
