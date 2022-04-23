package org.telegram.ui.Components;

import org.telegram.ui.Components.AlertsCreator;
import org.telegram.ui.Components.AutoDeletePopupWrapper;

public final /* synthetic */ class AutoDeletePopupWrapper$$ExternalSyntheticLambda7 implements AlertsCreator.ScheduleDatePickerDelegate {
    public final /* synthetic */ AutoDeletePopupWrapper.Callback f$0;

    public /* synthetic */ AutoDeletePopupWrapper$$ExternalSyntheticLambda7(AutoDeletePopupWrapper.Callback callback) {
        this.f$0 = callback;
    }

    public final void didSelectDate(boolean z, int i) {
        AutoDeletePopupWrapper.lambda$new$4(this.f$0, z, i);
    }
}
