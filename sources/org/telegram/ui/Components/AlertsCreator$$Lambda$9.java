package org.telegram.ui.Components;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import org.telegram.ui.Components.AlertsCreator.DatePickerDelegate;

final /* synthetic */ class AlertsCreator$$Lambda$9 implements OnClickListener {
    private final boolean arg$1;
    private final NumberPicker arg$2;
    private final NumberPicker arg$3;
    private final NumberPicker arg$4;
    private final DatePickerDelegate arg$5;

    AlertsCreator$$Lambda$9(boolean z, NumberPicker numberPicker, NumberPicker numberPicker2, NumberPicker numberPicker3, DatePickerDelegate datePickerDelegate) {
        this.arg$1 = z;
        this.arg$2 = numberPicker;
        this.arg$3 = numberPicker2;
        this.arg$4 = numberPicker3;
        this.arg$5 = datePickerDelegate;
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        AlertsCreator.lambda$createDatePickerDialog$9$AlertsCreator(this.arg$1, this.arg$2, this.arg$3, this.arg$4, this.arg$5, dialogInterface, i);
    }
}
