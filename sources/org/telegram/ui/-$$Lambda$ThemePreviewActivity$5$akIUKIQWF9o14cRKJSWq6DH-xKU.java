package org.telegram.ui;

import org.telegram.ui.Components.ColorPicker.BrightnessLimit;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$ThemePreviewActivity$5$akIUKIQWF9o14cRKJSWq6DH-xKU implements BrightnessLimit {
    public static final /* synthetic */ -$$Lambda$ThemePreviewActivity$5$akIUKIQWF9o14cRKJSWq6DH-xKU INSTANCE = new -$$Lambda$ThemePreviewActivity$5$akIUKIQWF9o14cRKJSWq6DH-xKU();

    private /* synthetic */ -$$Lambda$ThemePreviewActivity$5$akIUKIQWF9o14cRKJSWq6DH-xKU() {
    }

    public final float getLimit(int i, int i2, int i3) {
        return (255.0f / ((((((float) i) * 0.5f) + (((float) i2) * 0.8f)) + (((float) i3) * 0.1f)) + 500.0f));
    }
}
