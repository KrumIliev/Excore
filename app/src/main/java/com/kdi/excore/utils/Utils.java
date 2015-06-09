package com.kdi.excore.utils;

import android.graphics.Color;

import java.util.Random;

/**
 * Created by Krum Iliev on 5/25/2015.
 */
public class Utils {

    public static int getRandomColor(boolean transparent) {
        Random rnd = new Random();
        int alpha = transparent ? 210 : 255;
        return Color.argb(alpha, rnd.nextInt(150), rnd.nextInt(150), rnd.nextInt(150));
    }

    public static float calculatePointScale(float canvasSize, float deviceSize, float point) {
        float factor = canvasSize / deviceSize;
        return point * factor;
    }
}
