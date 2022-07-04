package org.telegram.ui;

import org.telegram.ui.Components.NumberPicker;

public final /* synthetic */ class GroupCallActivity$$ExternalSyntheticLambda53 implements NumberPicker.Formatter {
    public static final /* synthetic */ GroupCallActivity$$ExternalSyntheticLambda53 INSTANCE = new GroupCallActivity$$ExternalSyntheticLambda53();

    private /* synthetic */ GroupCallActivity$$ExternalSyntheticLambda53() {
    }

    public final String format(int i) {
        return String.format("%02d", new Object[]{Integer.valueOf(i)});
    }
}
