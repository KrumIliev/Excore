package com.kdi.excore.utils;

import android.graphics.Color;

import java.util.Random;

/**
 * Created by Krum Iliev on 5/25/2015.
 */
public class Utils {

    public static int getRandomColor() {
        Random rnd = new Random();
        return Color.argb(255, rnd.nextInt(150), rnd.nextInt(150), rnd.nextInt(150));
    }
}
