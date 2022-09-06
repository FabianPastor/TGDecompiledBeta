package org.telegram.ui.Components;

import java.util.Calendar;
import org.telegram.ui.Components.NumberPicker;

public final /* synthetic */ class AlertsCreator$$ExternalSyntheticLambda106 implements NumberPicker.Formatter {
    public final /* synthetic */ long f$0;
    public final /* synthetic */ Calendar f$1;
    public final /* synthetic */ int f$2;
    public final /* synthetic */ int f$3;

    public /* synthetic */ AlertsCreator$$ExternalSyntheticLambda106(long j, Calendar calendar, int i, int i2) {
        this.f$0 = j;
        this.f$1 = calendar;
        this.f$2 = i;
        this.f$3 = i2;
    }

    public final String format(int i) {
        return AlertsCreator.lambda$createStatusUntilDatePickerDialog$63(this.f$0, this.f$1, this.f$2, this.f$3, i);
    }
}
