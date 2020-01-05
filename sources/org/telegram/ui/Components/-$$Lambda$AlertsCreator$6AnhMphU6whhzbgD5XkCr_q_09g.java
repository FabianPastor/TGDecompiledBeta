package org.telegram.ui.Components;

import android.widget.TextView;
import org.telegram.ui.Components.NumberPicker.OnValueChangeListener;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$AlertsCreator$6AnhMphU6whhzbgD5XkCr_q_09g implements OnValueChangeListener {
    private final /* synthetic */ TextView f$0;
    private final /* synthetic */ int f$1;
    private final /* synthetic */ long f$2;
    private final /* synthetic */ NumberPicker f$3;
    private final /* synthetic */ NumberPicker f$4;
    private final /* synthetic */ NumberPicker f$5;

    public /* synthetic */ -$$Lambda$AlertsCreator$6AnhMphU6whhzbgD5XkCr_q_09g(TextView textView, int i, long j, NumberPicker numberPicker, NumberPicker numberPicker2, NumberPicker numberPicker3) {
        this.f$0 = textView;
        this.f$1 = i;
        this.f$2 = j;
        this.f$3 = numberPicker;
        this.f$4 = numberPicker2;
        this.f$5 = numberPicker3;
    }

    public final void onValueChange(NumberPicker numberPicker, int i, int i2) {
        AlertsCreator.lambda$createScheduleDatePickerDialog$25(this.f$0, this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, numberPicker, i, i2);
    }
}
