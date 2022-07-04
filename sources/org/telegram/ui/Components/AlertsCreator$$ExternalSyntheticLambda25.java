package org.telegram.ui.Components;

import android.widget.LinearLayout;
import org.telegram.ui.Components.NumberPicker;

public final /* synthetic */ class AlertsCreator$$ExternalSyntheticLambda25 implements NumberPicker.OnValueChangeListener {
    public final /* synthetic */ LinearLayout f$0;

    public /* synthetic */ AlertsCreator$$ExternalSyntheticLambda25(LinearLayout linearLayout) {
        this.f$0 = linearLayout;
    }

    public final void onValueChange(NumberPicker numberPicker, int i, int i2) {
        AlertsCreator.lambda$createMuteForPickerDialog$74(this.f$0, numberPicker, i, i2);
    }
}
