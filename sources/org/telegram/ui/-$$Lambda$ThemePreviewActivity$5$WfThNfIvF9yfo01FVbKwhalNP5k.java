package org.telegram.ui;

import org.telegram.ui.Components.ColorPicker.BrightnessLimit;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$ThemePreviewActivity$5$WfThNfIvF9yfo01FVbKwhalNP5k implements BrightnessLimit {
    public static final /* synthetic */ -$$Lambda$ThemePreviewActivity$5$WfThNfIvF9yfo01FVbKwhalNP5k INSTANCE = new -$$Lambda$ThemePreviewActivity$5$WfThNfIvF9yfo01FVbKwhalNP5k();

    private /* synthetic */ -$$Lambda$ThemePreviewActivity$5$WfThNfIvF9yfo01FVbKwhalNP5k() {
    }

    public final float getLimit(int i, int i2, int i3) {
        return (255.0f / ((((((float) i) * 0.1f) + (((float) i2) * 1.0f)) + (((float) i3) * 0.1f)) + 50.0f));
    }
}
