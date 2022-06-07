package org.telegram.ui;

import org.telegram.ui.Components.NumberPicker;

public final /* synthetic */ class GroupCallActivity$$ExternalSyntheticLambda59 implements NumberPicker.Formatter {
    public static final /* synthetic */ GroupCallActivity$$ExternalSyntheticLambda59 INSTANCE = new GroupCallActivity$$ExternalSyntheticLambda59();

    private /* synthetic */ GroupCallActivity$$ExternalSyntheticLambda59() {
    }

    public final String format(int i) {
        return String.format("%02d", new Object[]{Integer.valueOf(i)});
    }
}
