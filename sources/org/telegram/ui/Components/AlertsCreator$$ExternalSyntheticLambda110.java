package org.telegram.ui.Components;

import org.telegram.messenger.LocaleController;
import org.telegram.ui.Components.NumberPicker;

public final /* synthetic */ class AlertsCreator$$ExternalSyntheticLambda110 implements NumberPicker.Formatter {
    public static final /* synthetic */ AlertsCreator$$ExternalSyntheticLambda110 INSTANCE = new AlertsCreator$$ExternalSyntheticLambda110();

    private /* synthetic */ AlertsCreator$$ExternalSyntheticLambda110() {
    }

    public final String format(int i) {
        return LocaleController.getString("NotificationsFrequencyDivider", NUM);
    }
}
