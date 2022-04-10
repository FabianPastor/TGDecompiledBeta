package org.telegram.ui.Components;

import android.widget.LinearLayout;
import android.widget.TextView;
import org.telegram.ui.Components.NumberPicker;

public final /* synthetic */ class AlertsCreator$$ExternalSyntheticLambda126 implements NumberPicker.OnValueChangeListener {
    public final /* synthetic */ LinearLayout f$0;
    public final /* synthetic */ NumberPicker f$1;
    public final /* synthetic */ NumberPicker f$2;
    public final /* synthetic */ TextView f$3;

    public /* synthetic */ AlertsCreator$$ExternalSyntheticLambda126(LinearLayout linearLayout, NumberPicker numberPicker, NumberPicker numberPicker2, TextView textView) {
        this.f$0 = linearLayout;
        this.f$1 = numberPicker;
        this.f$2 = numberPicker2;
        this.f$3 = textView;
    }

    public final void onValueChange(NumberPicker numberPicker, int i, int i2) {
        AlertsCreator.lambda$createMuteForPickerDialog$74(this.f$0, this.f$1, this.f$2, this.f$3, numberPicker, i, i2);
    }
}
