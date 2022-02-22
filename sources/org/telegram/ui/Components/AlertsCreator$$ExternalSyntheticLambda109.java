package org.telegram.ui.Components;

import android.widget.LinearLayout;
import org.telegram.ui.Components.NumberPicker;

public final /* synthetic */ class AlertsCreator$$ExternalSyntheticLambda109 implements NumberPicker.OnValueChangeListener {
    public final /* synthetic */ LinearLayout f$0;
    public final /* synthetic */ NumberPicker f$1;
    public final /* synthetic */ NumberPicker f$2;
    public final /* synthetic */ NumberPicker f$3;

    public /* synthetic */ AlertsCreator$$ExternalSyntheticLambda109(LinearLayout linearLayout, NumberPicker numberPicker, NumberPicker numberPicker2, NumberPicker numberPicker3) {
        this.f$0 = linearLayout;
        this.f$1 = numberPicker;
        this.f$2 = numberPicker2;
        this.f$3 = numberPicker3;
    }

    public final void onValueChange(NumberPicker numberPicker, int i, int i2) {
        AlertsCreator.lambda$createDatePickerDialog$55(this.f$0, this.f$1, this.f$2, this.f$3, numberPicker, i, i2);
    }
}
