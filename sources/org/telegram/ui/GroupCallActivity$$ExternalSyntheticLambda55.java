package org.telegram.ui;

import org.telegram.ui.Components.NumberPicker;

public final /* synthetic */ class GroupCallActivity$$ExternalSyntheticLambda55 implements NumberPicker.Formatter {
    public static final /* synthetic */ GroupCallActivity$$ExternalSyntheticLambda55 INSTANCE = new GroupCallActivity$$ExternalSyntheticLambda55();

    private /* synthetic */ GroupCallActivity$$ExternalSyntheticLambda55() {
    }

    public final String format(int i) {
        return String.format("%02d", new Object[]{Integer.valueOf(i)});
    }
}
