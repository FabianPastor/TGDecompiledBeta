package org.telegram.ui.Components;

import org.telegram.ui.Components.NumberPicker.OnValueChangeListener;

final /* synthetic */ class AlertsCreator$$Lambda$5 implements OnValueChangeListener {
    private final NumberPicker arg$1;
    private final NumberPicker arg$2;
    private final NumberPicker arg$3;

    AlertsCreator$$Lambda$5(NumberPicker numberPicker, NumberPicker numberPicker2, NumberPicker numberPicker3) {
        this.arg$1 = numberPicker;
        this.arg$2 = numberPicker2;
        this.arg$3 = numberPicker3;
    }

    public void onValueChange(NumberPicker numberPicker, int i, int i2) {
        AlertsCreator.updateDayPicker(this.arg$1, this.arg$2, this.arg$3);
    }
}
