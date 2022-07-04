package org.telegram.ui.Components;

import android.widget.LinearLayout;
import android.widget.TextView;
import org.telegram.ui.Components.NumberPicker;

public final /* synthetic */ class AlertsCreator$$ExternalSyntheticLambda28 implements NumberPicker.OnValueChangeListener {
    public final /* synthetic */ LinearLayout f$0;
    public final /* synthetic */ TextView f$1;
    public final /* synthetic */ long f$2;
    public final /* synthetic */ long f$3;
    public final /* synthetic */ NumberPicker f$4;
    public final /* synthetic */ NumberPicker f$5;
    public final /* synthetic */ NumberPicker f$6;

    public /* synthetic */ AlertsCreator$$ExternalSyntheticLambda28(LinearLayout linearLayout, TextView textView, long j, long j2, NumberPicker numberPicker, NumberPicker numberPicker2, NumberPicker numberPicker3) {
        this.f$0 = linearLayout;
        this.f$1 = textView;
        this.f$2 = j;
        this.f$3 = j2;
        this.f$4 = numberPicker;
        this.f$5 = numberPicker2;
        this.f$6 = numberPicker3;
    }

    public final void onValueChange(NumberPicker numberPicker, int i, int i2) {
        AlertsCreator.lambda$createScheduleDatePickerDialog$51(this.f$0, this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, numberPicker, i, i2);
    }
}
