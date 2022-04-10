package org.telegram.ui.Components;

import org.telegram.messenger.LocaleController;
import org.telegram.ui.Components.NumberPicker;

public final /* synthetic */ class AlertsCreator$$ExternalSyntheticLambda118 implements NumberPicker.Formatter {
    public static final /* synthetic */ AlertsCreator$$ExternalSyntheticLambda118 INSTANCE = new AlertsCreator$$ExternalSyntheticLambda118();

    private /* synthetic */ AlertsCreator$$ExternalSyntheticLambda118() {
    }

    public final String format(int i) {
        return LocaleController.formatPluralString("Times", i + 1);
    }
}
