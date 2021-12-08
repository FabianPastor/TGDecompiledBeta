package org.telegram.ui.Components;

import org.telegram.ui.Components.NumberPicker;

public final /* synthetic */ class AlertsCreator$$ExternalSyntheticLambda99 implements NumberPicker.OnScrollListener {
    public final /* synthetic */ boolean f$0;
    public final /* synthetic */ NumberPicker f$1;
    public final /* synthetic */ NumberPicker f$2;
    public final /* synthetic */ NumberPicker f$3;

    public /* synthetic */ AlertsCreator$$ExternalSyntheticLambda99(boolean z, NumberPicker numberPicker, NumberPicker numberPicker2, NumberPicker numberPicker3) {
        this.f$0 = z;
        this.f$1 = numberPicker;
        this.f$2 = numberPicker2;
        this.f$3 = numberPicker3;
    }

    public final void onScrollStateChange(NumberPicker numberPicker, int i) {
        AlertsCreator.lambda$createDatePickerDialog$33(this.f$0, this.f$1, this.f$2, this.f$3, numberPicker, i);
    }
}
