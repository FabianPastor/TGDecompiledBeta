package org.telegram.ui.Components;

import org.telegram.ui.Components.NumberPicker;

public final /* synthetic */ class AlertsCreator$$ExternalSyntheticLambda90 implements NumberPicker.Formatter {
    public static final /* synthetic */ AlertsCreator$$ExternalSyntheticLambda90 INSTANCE = new AlertsCreator$$ExternalSyntheticLambda90();

    private /* synthetic */ AlertsCreator$$ExternalSyntheticLambda90() {
    }

    public final String format(int i) {
        return String.format("%02d", new Object[]{Integer.valueOf(i)});
    }
}
