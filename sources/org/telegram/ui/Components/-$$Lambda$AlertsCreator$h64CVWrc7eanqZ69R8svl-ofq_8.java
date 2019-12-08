package org.telegram.ui.Components;

import java.util.Calendar;
import org.telegram.ui.Components.NumberPicker.Formatter;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$AlertsCreator$h64CVWrc7eanqZ69R8svl-ofq_8 implements Formatter {
    private final /* synthetic */ Calendar f$0;
    private final /* synthetic */ int f$1;

    public /* synthetic */ -$$Lambda$AlertsCreator$h64CVWrc7eanqZ69R8svl-ofq_8(Calendar calendar, int i) {
        this.f$0 = calendar;
        this.f$1 = i;
    }

    public final String format(int i) {
        return AlertsCreator.lambda$createScheduleDatePickerDialog$22(this.f$0, this.f$1, i);
    }
}
