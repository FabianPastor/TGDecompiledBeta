package org.telegram.ui.Components;

import org.telegram.ui.Components.NumberPicker;

public final /* synthetic */ class AlertsCreator$$ExternalSyntheticLambda113 implements NumberPicker.Formatter {
    public static final /* synthetic */ AlertsCreator$$ExternalSyntheticLambda113 INSTANCE = new AlertsCreator$$ExternalSyntheticLambda113();

    private /* synthetic */ AlertsCreator$$ExternalSyntheticLambda113() {
    }

    public final String format(int i) {
        return String.format("%02d", new Object[]{Integer.valueOf(i)});
    }
}
