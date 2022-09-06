package org.telegram.ui.Components;

import org.telegram.ui.Components.NumberPicker;

public final /* synthetic */ class AlertsCreator$$ExternalSyntheticLambda117 implements NumberPicker.Formatter {
    public static final /* synthetic */ AlertsCreator$$ExternalSyntheticLambda117 INSTANCE = new AlertsCreator$$ExternalSyntheticLambda117();

    private /* synthetic */ AlertsCreator$$ExternalSyntheticLambda117() {
    }

    public final String format(int i) {
        return String.format("%02d", new Object[]{Integer.valueOf(i)});
    }
}
