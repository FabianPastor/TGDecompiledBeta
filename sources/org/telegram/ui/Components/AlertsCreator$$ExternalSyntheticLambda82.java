package org.telegram.ui.Components;

import java.util.Calendar;
import org.telegram.ui.Components.NumberPicker;

public final /* synthetic */ class AlertsCreator$$ExternalSyntheticLambda82 implements NumberPicker.Formatter {
    public final /* synthetic */ long f$0;
    public final /* synthetic */ Calendar f$1;
    public final /* synthetic */ int f$2;

    public /* synthetic */ AlertsCreator$$ExternalSyntheticLambda82(long j, Calendar calendar, int i) {
        this.f$0 = j;
        this.f$1 = calendar;
        this.f$2 = i;
    }

    public final String format(int i) {
        return AlertsCreator.lambda$createDatePickerDialog$47(this.f$0, this.f$1, this.f$2, i);
    }
}
