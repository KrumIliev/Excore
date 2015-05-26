package com.kdi.excore.xfx;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Build;

import com.kdi.excore.R;

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


    private Context context;
    private MediaPlayer mediaPlayer; // for playing music

    private SoundPool soundPool; // for playing short audio files like effects
    private int soundIds[];

    public AudioPlayer(Context context) {
        this.context = context;

        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.LOLLIPOP) {
            SoundPool.Builder poolBuilder = new SoundPool.Builder();
            poolBuilder.setMaxStreams(10);
            AudioAttributes.Builder audioAttributesBuilder = new AudioAttributes.Builder();
            audioAttributesBuilder.setUsage(AudioAttributes.USAGE_GAME);
            audioAttributesBuilder.setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION);
            poolBuilder.setAudioAttributes(audioAttributesBuilder.build());
            soundPool = poolBuilder.build();
        } else {
            soundPool = new SoundPool(10, AudioManager.STREAM_MUSIC, 0);
        }

        soundIds = new int[10];
        soundIds[SOUND_WEAPON_0] = soundPool.load(context, R.raw.shoot_0, 1);
        soundIds[SOUND_WEAPON_1] = soundPool.load(context, R.raw.shoot_1, 1);
        soundIds[SOUND_WEAPON_2] = soundPool.load(context, R.raw.shoot_2, 1);
        soundIds[SOUND_WEAPON_3] = soundPool.load(context, R.raw.shoot_3, 1);
        soundIds[SOUND_WEAPON_4] = soundPool.load(context, R.raw.shoot_4, 1);
        soundIds[POWER_UP_DESTROY] = soundPool.load(context, R.raw.p_destroy, 1);
        soundIds[POWER_UP_SLOW] = soundPool.load(context, R.raw.p_slow, 1);
        soundIds[POWER_UP_FAST] = soundPool.load(context, R.raw.p_fast, 1);
    }

    public void playMusic(int id) {
        mediaPlayer = MediaPlayer.create(context, id);
        mediaPlayer.setLooping(true);
        mediaPlayer.setVolume(100, 100);
        mediaPlayer.start();
    }

    public void stopMusic() {
        if (mediaPlayer != null) mediaPlayer.release();
    }

    public void playSound(int sound) {
        soundPool.play(soundIds[sound], 1, 1, 1, 0, 1f);
    }

    public void dispose() {
        if (mediaPlayer != null) mediaPlayer.release();
        if (soundPool != null) soundPool.release();
    }


}
