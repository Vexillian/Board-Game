package com.example.charactersheet;

import android.content.Context;

public interface DefeatStrategy {
    void applyEffectAfterDefeat(SharedViewModel sharedViewModel, Context context, int selectedZone, Enemy enemy);
}
