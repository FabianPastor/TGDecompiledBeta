package org.telegram.ui.Components;

import org.telegram.messenger.LocaleController;
import org.telegram.ui.Components.NumberPicker;

public final /* synthetic */ class AlertsCreator$$ExternalSyntheticLambda105 implements NumberPicker.Formatter {
    public static final /* synthetic */ AlertsCreator$$ExternalSyntheticLambda105 INSTANCE = new AlertsCreator$$ExternalSyntheticLambda105();

    private /* synthetic */ AlertsCreator$$ExternalSyntheticLambda105() {
    }

    public final String format(int i) {
        return LocaleController.formatPluralString("Hours", i);
    }
}
