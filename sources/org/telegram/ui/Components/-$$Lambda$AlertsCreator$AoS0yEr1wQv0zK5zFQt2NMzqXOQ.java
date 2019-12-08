package org.telegram.ui.Components;

import android.widget.TextView;
import org.telegram.ui.Components.NumberPicker.OnValueChangeListener;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$AlertsCreator$AoS0yEr1wQv0zK5zFQt2NMzqXOQ implements OnValueChangeListener {
    private final /* synthetic */ TextView f$0;
    private final /* synthetic */ boolean f$1;
    private final /* synthetic */ NumberPicker f$2;
    private final /* synthetic */ NumberPicker f$3;
    private final /* synthetic */ NumberPicker f$4;

    public /* synthetic */ -$$Lambda$AlertsCreator$AoS0yEr1wQv0zK5zFQt2NMzqXOQ(TextView textView, boolean z, NumberPicker numberPicker, NumberPicker numberPicker2, NumberPicker numberPicker3) {
        this.f$0 = textView;
        this.f$1 = z;
        this.f$2 = numberPicker;
        this.f$3 = numberPicker2;
        this.f$4 = numberPicker3;
    }

    public final void onValueChange(NumberPicker numberPicker, int i, int i2) {
        AlertsCreator.checkScheduleDate(this.f$0, this.f$1, this.f$2, this.f$3, this.f$4);
    }
}
