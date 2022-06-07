package org.telegram.ui.Components;

import org.telegram.messenger.LocaleController;
import org.telegram.ui.Components.NumberPicker;

public final /* synthetic */ class AlertsCreator$$ExternalSyntheticLambda107 implements NumberPicker.Formatter {
    public static final /* synthetic */ AlertsCreator$$ExternalSyntheticLambda107 INSTANCE = new AlertsCreator$$ExternalSyntheticLambda107();

    private /* synthetic */ AlertsCreator$$ExternalSyntheticLambda107() {
    }

    public final String format(int i) {
        return LocaleController.formatPluralString("Minutes", i + 1, new Object[0]);
    }
}
