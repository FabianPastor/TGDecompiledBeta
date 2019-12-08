package org.telegram.ui;

import org.telegram.ui.Components.ColorPicker.ColorPickerDelegate;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$ThemePreviewActivity$3CSXyaKKfzRGi7ekY0M7EIdLxs0 implements ColorPickerDelegate {
    private final /* synthetic */ ThemePreviewActivity f$0;

    public /* synthetic */ -$$Lambda$ThemePreviewActivity$3CSXyaKKfzRGi7ekY0M7EIdLxs0(ThemePreviewActivity themePreviewActivity) {
        this.f$0 = themePreviewActivity;
    }

    public final void setColor(int i, int i2, boolean z) {
        this.f$0.scheduleApplyColor(i, i2, z);
    }
}
