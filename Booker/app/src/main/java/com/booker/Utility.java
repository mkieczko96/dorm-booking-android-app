package com.booker;

import android.content.Context;
import android.content.res.Resources;
import android.util.TypedValue;

import org.jetbrains.annotations.NotNull;

public class Utility {

    public static float dpToPx(@NotNull Context context, float dp) {
        Resources r = context.getResources();
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics());
    }
}
