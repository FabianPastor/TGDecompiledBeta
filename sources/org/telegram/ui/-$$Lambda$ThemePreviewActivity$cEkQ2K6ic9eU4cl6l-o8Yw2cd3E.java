package org.telegram.ui;

import org.telegram.ui.Components.ColorPicker.ColorPickerDelegate;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$ThemePreviewActivity$cEkQ2K6ic9eU4cl6l-o8Yw2cd3E implements ColorPickerDelegate {
    private final /* synthetic */ ThemePreviewActivity f$0;

    public /* synthetic */ -$$Lambda$ThemePreviewActivity$cEkQ2K6ic9eU4cl6l-o8Yw2cd3E(ThemePreviewActivity themePreviewActivity) {
        this.f$0 = themePreviewActivity;
    }

    public final void setColor(int i) {
        this.f$0.scheduleApplyAccent(i);
    }
}
