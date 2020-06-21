package vnu.uet.mobilecourse.assistant.util;

import android.content.Context;
import android.util.DisplayMetrics;
import android.util.TypedValue;

public class DimensionUtils {

    public static int dpToPx(float dp, Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, displayMetrics);
    }

    public static int spToPx(float sp, Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, displayMetrics);
    }

    public static int dpToSp(float dp, Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        return (int) (dpToPx(dp, context) / displayMetrics.scaledDensity);
    }
}
