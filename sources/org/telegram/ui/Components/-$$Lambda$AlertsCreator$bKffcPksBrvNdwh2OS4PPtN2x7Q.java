package org.telegram.ui.Components;

import org.telegram.ui.Components.NumberPicker.OnValueChangeListener;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$AlertsCreator$bKffcPksBrvNdwh2OS4PPtN2x7Q implements OnValueChangeListener {
    private final /* synthetic */ NumberPicker f$0;
    private final /* synthetic */ NumberPicker f$1;
    private final /* synthetic */ NumberPicker f$2;

    public /* synthetic */ -$$Lambda$AlertsCreator$bKffcPksBrvNdwh2OS4PPtN2x7Q(NumberPicker numberPicker, NumberPicker numberPicker2, NumberPicker numberPicker3) {
        this.f$0 = numberPicker;
        this.f$1 = numberPicker2;
        this.f$2 = numberPicker3;
    }

    public final void onValueChange(NumberPicker numberPicker, int i, int i2) {
        AlertsCreator.updateDayPicker(this.f$0, this.f$1, this.f$2);
    }
}
