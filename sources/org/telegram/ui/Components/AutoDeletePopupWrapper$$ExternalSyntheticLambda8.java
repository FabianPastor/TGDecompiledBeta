package org.telegram.ui.Components;

import org.telegram.ui.Components.AlertsCreator;
import org.telegram.ui.Components.AutoDeletePopupWrapper;

public final /* synthetic */ class AutoDeletePopupWrapper$$ExternalSyntheticLambda8 implements AlertsCreator.ScheduleDatePickerDelegate {
    public final /* synthetic */ AutoDeletePopupWrapper.Callback f$0;

    public /* synthetic */ AutoDeletePopupWrapper$$ExternalSyntheticLambda8(AutoDeletePopupWrapper.Callback callback) {
        this.f$0 = callback;
    }

    public final void didSelectDate(boolean z, int i) {
        AutoDeletePopupWrapper.lambda$new$5(this.f$0, z, i);
    }
}
