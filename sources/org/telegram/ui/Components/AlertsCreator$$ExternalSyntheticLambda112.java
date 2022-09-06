package org.telegram.ui.Components;

import org.telegram.messenger.LocaleController;
import org.telegram.ui.Components.NumberPicker;

public final /* synthetic */ class AlertsCreator$$ExternalSyntheticLambda112 implements NumberPicker.Formatter {
    public static final /* synthetic */ AlertsCreator$$ExternalSyntheticLambda112 INSTANCE = new AlertsCreator$$ExternalSyntheticLambda112();

    private /* synthetic */ AlertsCreator$$ExternalSyntheticLambda112() {
    }

    public final String format(int i) {
        return LocaleController.formatPluralString("Minutes", i + 1, new Object[0]);
    }
}
