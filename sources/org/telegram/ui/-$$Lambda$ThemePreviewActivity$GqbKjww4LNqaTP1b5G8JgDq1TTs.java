package org.telegram.ui;

import org.telegram.ui.Components.ColorPicker.BrightnessLimit;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$ThemePreviewActivity$GqbKjww4LNqaTP1b5G8JgDq1TTs implements BrightnessLimit {
    public static final /* synthetic */ -$$Lambda$ThemePreviewActivity$GqbKjww4LNqaTP1b5G8JgDq1TTs INSTANCE = new -$$Lambda$ThemePreviewActivity$GqbKjww4LNqaTP1b5G8JgDq1TTs();

    private /* synthetic */ -$$Lambda$ThemePreviewActivity$GqbKjww4LNqaTP1b5G8JgDq1TTs() {
    }

    public final float getLimit(int i, int i2, int i3) {
        return (255.0f / ((((((float) i) * 0.1f) + (((float) i2) * 1.0f)) + (((float) i3) * 0.1f)) + 50.0f));
    }
}
