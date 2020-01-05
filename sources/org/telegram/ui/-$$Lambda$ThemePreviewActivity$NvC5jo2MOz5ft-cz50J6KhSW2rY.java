package org.telegram.ui;

import org.telegram.ui.Components.ColorPicker.BrightnessLimit;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$ThemePreviewActivity$NvC5jo2MOz5ft-cz50J6KhSW2rY implements BrightnessLimit {
    public static final /* synthetic */ -$$Lambda$ThemePreviewActivity$NvC5jo2MOz5ft-cz50J6KhSW2rY INSTANCE = new -$$Lambda$ThemePreviewActivity$NvC5jo2MOz5ft-cz50J6KhSW2rY();

    private /* synthetic */ -$$Lambda$ThemePreviewActivity$NvC5jo2MOz5ft-cz50J6KhSW2rY() {
    }

    public final float getLimit(int i, int i2, int i3) {
        return (255.0f / ((((((float) i) * 0.5f) + (((float) i2) * 0.8f)) + (((float) i3) * 0.1f)) + 500.0f));
    }
}
