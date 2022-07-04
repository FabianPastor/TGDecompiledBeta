package org.telegram.ui.Components;

import org.telegram.messenger.LocaleController;
import org.telegram.ui.Components.NumberPicker;

public final /* synthetic */ class AlertsCreator$$ExternalSyntheticLambda18 implements NumberPicker.Formatter {
    public static final /* synthetic */ AlertsCreator$$ExternalSyntheticLambda18 INSTANCE = new AlertsCreator$$ExternalSyntheticLambda18();

    private /* synthetic */ AlertsCreator$$ExternalSyntheticLambda18() {
    }

    public final String format(int i) {
        return LocaleController.getString("NotificationsFrequencyDivider", NUM);
    }
}
