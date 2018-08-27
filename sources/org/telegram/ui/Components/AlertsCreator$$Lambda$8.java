package org.telegram.ui.Components;

import org.telegram.ui.Components.NumberPicker.OnScrollListener;

final /* synthetic */ class AlertsCreator$$Lambda$8 implements OnScrollListener {
    private final boolean arg$1;
    private final NumberPicker arg$2;
    private final NumberPicker arg$3;
    private final NumberPicker arg$4;

    AlertsCreator$$Lambda$8(boolean z, NumberPicker numberPicker, NumberPicker numberPicker2, NumberPicker numberPicker3) {
        this.arg$1 = z;
        this.arg$2 = numberPicker;
        this.arg$3 = numberPicker2;
        this.arg$4 = numberPicker3;
    }

    public void onScrollStateChange(NumberPicker numberPicker, int i) {
        AlertsCreator.lambda$createDatePickerDialog$8$AlertsCreator(this.arg$1, this.arg$2, this.arg$3, this.arg$4, numberPicker, i);
    }
}
