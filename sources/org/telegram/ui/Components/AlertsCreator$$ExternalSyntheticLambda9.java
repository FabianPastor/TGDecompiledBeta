package org.telegram.ui.Components;

import org.telegram.ui.Components.NumberPicker;

public final /* synthetic */ class AlertsCreator$$ExternalSyntheticLambda9 implements NumberPicker.Formatter {
    public static final /* synthetic */ AlertsCreator$$ExternalSyntheticLambda9 INSTANCE = new AlertsCreator$$ExternalSyntheticLambda9();

    private /* synthetic */ AlertsCreator$$ExternalSyntheticLambda9() {
    }

    public final String format(int i) {
        return String.format("%02d", new Object[]{Integer.valueOf(i)});
    }
}
