package org.telegram.ui.Components;

import org.telegram.ui.Components.NumberPicker;

public final /* synthetic */ class AlertsCreator$$ExternalSyntheticLambda96 implements NumberPicker.Formatter {
    public static final /* synthetic */ AlertsCreator$$ExternalSyntheticLambda96 INSTANCE = new AlertsCreator$$ExternalSyntheticLambda96();

    private /* synthetic */ AlertsCreator$$ExternalSyntheticLambda96() {
    }

    public final String format(int i) {
        return String.format("%02d", new Object[]{Integer.valueOf(i)});
    }
}
