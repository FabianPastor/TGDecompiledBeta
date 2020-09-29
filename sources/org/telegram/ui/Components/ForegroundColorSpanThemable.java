package org.telegram.ui.Components;

import android.text.TextPaint;
import android.text.style.CharacterStyle;
import android.text.style.UpdateAppearance;
import org.telegram.ui.ActionBar.Theme;

public class ForegroundColorSpanThemable extends CharacterStyle implements UpdateAppearance {
    int color;
    String colorKey;

    public ForegroundColorSpanThemable(String str) {
        this.colorKey = str;
    }

    public void updateDrawState(TextPaint textPaint) {
        this.color = Theme.getColor(this.colorKey);
        int color2 = textPaint.getColor();
        int i = this.color;
        if (color2 != i) {
            textPaint.setColor(i);
        }
    }
}
