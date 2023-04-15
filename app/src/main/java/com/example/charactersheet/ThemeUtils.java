package com.example.charactersheet;

import android.content.Context;
import android.util.TypedValue;

public class ThemeUtils {
    public static int obtainColorPrimary(Context context) {
        TypedValue typedValue = new TypedValue();
        context.getTheme().resolveAttribute(android.R.attr.colorPrimary, typedValue, true);
        return typedValue.data;
    }
}
