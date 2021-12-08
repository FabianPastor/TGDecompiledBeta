package org.telegram.ui.Components;

import android.text.TextPaint;
import android.text.style.CharacterStyle;
import android.text.style.UpdateAppearance;
import org.telegram.ui.ActionBar.Theme;

public class ForegroundColorSpanThemable extends CharacterStyle implements UpdateAppearance {
    private int color;
    private String colorKey;
    private final Theme.ResourcesProvider resourcesProvider;

    public ForegroundColorSpanThemable(String colorKey2) {
        this(colorKey2, (Theme.ResourcesProvider) null);
    }

    public ForegroundColorSpanThemable(String colorKey2, Theme.ResourcesProvider resourcesProvider2) {
        this.colorKey = colorKey2;
        this.resourcesProvider = resourcesProvider2;
    }

    public void updateDrawState(TextPaint textPaint) {
        this.color = Theme.getColor(this.colorKey, this.resourcesProvider);
        int color2 = textPaint.getColor();
        int i = this.color;
        if (color2 != i) {
            textPaint.setColor(i);
        }
    }
}
