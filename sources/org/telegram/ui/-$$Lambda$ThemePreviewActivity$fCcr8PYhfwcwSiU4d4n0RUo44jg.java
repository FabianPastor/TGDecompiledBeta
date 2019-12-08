package org.telegram.ui;

import org.telegram.ui.Components.ColorPicker.BrightnessLimit;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$ThemePreviewActivity$fCcr8PYhfwcwSiU4d4n0RUo44jg implements BrightnessLimit {
    public static final /* synthetic */ -$$Lambda$ThemePreviewActivity$fCcr8PYhfwcwSiU4d4n0RUo44jg INSTANCE = new -$$Lambda$ThemePreviewActivity$fCcr8PYhfwcwSiU4d4n0RUo44jg();

    private /* synthetic */ -$$Lambda$ThemePreviewActivity$fCcr8PYhfwcwSiU4d4n0RUo44jg() {
    }

    public final float getLimit(int i, int i2, int i3) {
        return (255.0f / ((((((float) i) * 0.5f) + (((float) i2) * 0.8f)) + (((float) i3) * 0.1f)) + 500.0f));
    }
}
