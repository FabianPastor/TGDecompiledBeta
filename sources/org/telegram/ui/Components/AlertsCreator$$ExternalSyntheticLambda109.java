package org.telegram.ui.Components;

import org.telegram.messenger.LocaleController;
import org.telegram.messenger.R;
import org.telegram.ui.Components.NumberPicker;

public final /* synthetic */ class AlertsCreator$$ExternalSyntheticLambda109 implements NumberPicker.Formatter {
    public static final /* synthetic */ AlertsCreator$$ExternalSyntheticLambda109 INSTANCE = new AlertsCreator$$ExternalSyntheticLambda109();

    private /* synthetic */ AlertsCreator$$ExternalSyntheticLambda109() {
    }

    public final String format(int i) {
        return LocaleController.getString("NotificationsFrequencyDivider", R.string.NotificationsFrequencyDivider);
    }
}
