package org.telegram.ui;

import org.telegram.ui.Components.ColorPicker.BrightnessLimit;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$ThemePreviewActivity$x6lCBnVBm9tdSmRKQA7EQNMGem0 implements BrightnessLimit {
    public static final /* synthetic */ -$$Lambda$ThemePreviewActivity$x6lCBnVBm9tdSmRKQA7EQNMGem0 INSTANCE = new -$$Lambda$ThemePreviewActivity$x6lCBnVBm9tdSmRKQA7EQNMGem0();

    private /* synthetic */ -$$Lambda$ThemePreviewActivity$x6lCBnVBm9tdSmRKQA7EQNMGem0() {
    }

    public final float getLimit(int i, int i2, int i3) {
        return (255.0f / ((((((float) i) * 0.1f) + (((float) i2) * 1.0f)) + (((float) i3) * 0.1f)) + 50.0f));
    }
}
