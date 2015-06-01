package com.kdi.excore.utils;

import android.graphics.Color;

import java.util.Random;

/**
 * Created by Krum Iliev on 5/25/2015.
 */
public class ColorUtils {

    public static int getRandomColor(boolean transparent) {
        Random rnd = new Random();
        int alpha = transparent ? 210 : 255;
        return Color.argb(alpha, rnd.nextInt(150), rnd.nextInt(150), rnd.nextInt(150));
    }

    public static int getRandomColor() {
        Random rnd = new Random();
        return Color.argb(255, rnd.nextInt(255), rnd.nextInt(255), rnd.nextInt(255));
    }
}
