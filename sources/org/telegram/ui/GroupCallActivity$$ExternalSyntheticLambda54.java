package org.telegram.ui;

import org.telegram.ui.Components.NumberPicker;

public final /* synthetic */ class GroupCallActivity$$ExternalSyntheticLambda54 implements NumberPicker.Formatter {
    public static final /* synthetic */ GroupCallActivity$$ExternalSyntheticLambda54 INSTANCE = new GroupCallActivity$$ExternalSyntheticLambda54();

    private /* synthetic */ GroupCallActivity$$ExternalSyntheticLambda54() {
    }

    public final String format(int i) {
        return String.format("%02d", new Object[]{Integer.valueOf(i)});
    }
}
