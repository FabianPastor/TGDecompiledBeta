package org.telegram.ui;

import org.telegram.ui.Components.NumberPicker;

public final /* synthetic */ class GroupCallActivity$$ExternalSyntheticLambda58 implements NumberPicker.Formatter {
    public static final /* synthetic */ GroupCallActivity$$ExternalSyntheticLambda58 INSTANCE = new GroupCallActivity$$ExternalSyntheticLambda58();

    private /* synthetic */ GroupCallActivity$$ExternalSyntheticLambda58() {
    }

    public final String format(int i) {
        return String.format("%02d", new Object[]{Integer.valueOf(i)});
    }
}
