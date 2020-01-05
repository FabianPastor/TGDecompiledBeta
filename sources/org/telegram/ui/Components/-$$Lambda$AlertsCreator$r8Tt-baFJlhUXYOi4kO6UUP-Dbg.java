package org.telegram.ui.Components;

import java.util.Calendar;
import org.telegram.ui.Components.NumberPicker.Formatter;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$AlertsCreator$r8Tt-baFJlhUXYOi4kO6UUP-Dbg implements Formatter {
    private final /* synthetic */ long f$0;
    private final /* synthetic */ Calendar f$1;
    private final /* synthetic */ int f$2;

    public /* synthetic */ -$$Lambda$AlertsCreator$r8Tt-baFJlhUXYOi4kO6UUP-Dbg(long j, Calendar calendar, int i) {
        this.f$0 = j;
        this.f$1 = calendar;
        this.f$2 = i;
    }

    public final String format(int i) {
        return AlertsCreator.lambda$createScheduleDatePickerDialog$24(this.f$0, this.f$1, this.f$2, i);
    }
}
