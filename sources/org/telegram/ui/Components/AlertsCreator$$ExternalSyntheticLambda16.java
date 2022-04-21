package org.telegram.ui.Components;

import org.telegram.messenger.LocaleController;
import org.telegram.ui.Components.NumberPicker;

public final /* synthetic */ class AlertsCreator$$ExternalSyntheticLambda16 implements NumberPicker.Formatter {
    public static final /* synthetic */ AlertsCreator$$ExternalSyntheticLambda16 INSTANCE = new AlertsCreator$$ExternalSyntheticLambda16();

    private /* synthetic */ AlertsCreator$$ExternalSyntheticLambda16() {
    }

    public final String format(int i) {
        return LocaleController.formatPluralString("Times", i + 1);
    }
}
