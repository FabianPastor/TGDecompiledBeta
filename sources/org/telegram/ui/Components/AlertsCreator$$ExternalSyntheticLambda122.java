package org.telegram.ui.Components;

import org.telegram.messenger.LocaleController;
import org.telegram.ui.Components.NumberPicker;

public final /* synthetic */ class AlertsCreator$$ExternalSyntheticLambda122 implements NumberPicker.Formatter {
    public static final /* synthetic */ AlertsCreator$$ExternalSyntheticLambda122 INSTANCE = new AlertsCreator$$ExternalSyntheticLambda122();

    private /* synthetic */ AlertsCreator$$ExternalSyntheticLambda122() {
    }

    public final String format(int i) {
        return LocaleController.formatPluralString("Times", i + 1, new Object[0]);
    }
}
