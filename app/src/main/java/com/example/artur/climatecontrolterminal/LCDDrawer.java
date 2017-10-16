package com.example.artur.climatecontrolterminal;

import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.support.v4.content.res.ResourcesCompat;

import java.util.ArrayList;
import java.util.List;

class LCDDrawer {
    LCDDrawer(Resources res) {
        drawableArray = new ArrayList<>();
        for (int resSegment : resSegments) {
            if (resSegment != -1) {
                drawableArray.add(ResourcesCompat.getDrawable(res, resSegment, null));
            } else {
                drawableArray.add(null);
            }
        }
    }

    LayerDrawable GetDrawable(DeviceReport report) {
        List<Drawable> dr = new ArrayList<>();

        int i = 0;
        for (int com = 0; com < 3; com++) {
            for (int line = 1; line < 26; line++, i++) {
                if (report.LCDV(com, line) && resSegments[i] != -1) {
                    dr.add(drawableArray.get(i));
                }
            }
        }

        return new LayerDrawable(dr.toArray(new Drawable[0]));
    }

    private List<Drawable> drawableArray;

    private static final int resSegments[] = {
            -1,
            R.drawable.d0c2,
            R.drawable.d0c3,
            -1,
            R.drawable.d0c5,
            -1,
            -1,
            R.drawable.d0c8,
            -1,
            R.drawable.d0c10,
            R.drawable.d0c11,
            R.drawable.d0c12,
            R.drawable.d0c13,
            R.drawable.d0c14,
            R.drawable.d0c15,
            R.drawable.d0c16,
            R.drawable.d0c17,
            R.drawable.d0c18,
            R.drawable.d0c19,
            R.drawable.d0c20,
            R.drawable.d0c21,
            R.drawable.d0c22,
            R.drawable.d0c23,
            R.drawable.d0c24,
            R.drawable.d0c25,
            R.drawable.d1c1,
            R.drawable.d1c2,
            R.drawable.d1c3,
            R.drawable.d1c4,
            R.drawable.d1c5,
            R.drawable.d1c6,
            R.drawable.d1c7,
            R.drawable.d1c8,
            R.drawable.d1c9,
            R.drawable.d1c10,
            R.drawable.d1c11,
            R.drawable.d1c12,
            R.drawable.d1c13,
            R.drawable.d1c14,
            R.drawable.d1c15,
            R.drawable.d1c16,
            R.drawable.d1c17,
            R.drawable.d1c18,
            R.drawable.d1c19,
            R.drawable.d1c20,
            R.drawable.d1c21,
            R.drawable.d1c22,
            R.drawable.d1c23,
            R.drawable.d1c24,
            R.drawable.d1c25,
            R.drawable.d2c1,
            R.drawable.d2c2,
            R.drawable.d2c3,
            R.drawable.d2c4,
            R.drawable.d2c5,
            R.drawable.d2c6,
            R.drawable.d2c7,
            R.drawable.d2c8,
            R.drawable.d2c9,
            R.drawable.d2c10,
            R.drawable.d2c11,
            R.drawable.d2c12,
            R.drawable.d2c13,
            R.drawable.d2c14,
            R.drawable.d2c15,
            R.drawable.d2c16,
            R.drawable.d2c17,
            R.drawable.d2c18,
            R.drawable.d2c19,
            R.drawable.d2c20,
            R.drawable.d2c21,
            R.drawable.d2c22,
            R.drawable.d2c23,
            R.drawable.d2c24,
            R.drawable.d2c25,
    };
}