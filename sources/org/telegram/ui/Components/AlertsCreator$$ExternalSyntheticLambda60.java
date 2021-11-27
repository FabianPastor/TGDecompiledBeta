package org.telegram.ui.Components;

import android.view.View;
import java.util.Calendar;
import org.telegram.ui.ActionBar.BottomSheet;
import org.telegram.ui.Components.AlertsCreator;

public final /* synthetic */ class AlertsCreator$$ExternalSyntheticLambda60 implements View.OnClickListener {
    public final /* synthetic */ boolean[] f$0;
    public final /* synthetic */ long f$1;
    public final /* synthetic */ long f$2;
    public final /* synthetic */ NumberPicker f$3;
    public final /* synthetic */ NumberPicker f$4;
    public final /* synthetic */ NumberPicker f$5;
    public final /* synthetic */ Calendar f$6;
    public final /* synthetic */ AlertsCreator.ScheduleDatePickerDelegate f$7;
    public final /* synthetic */ BottomSheet.Builder f$8;

    public /* synthetic */ AlertsCreator$$ExternalSyntheticLambda60(boolean[] zArr, long j, long j2, NumberPicker numberPicker, NumberPicker numberPicker2, NumberPicker numberPicker3, Calendar calendar, AlertsCreator.ScheduleDatePickerDelegate scheduleDatePickerDelegate, BottomSheet.Builder builder) {
        this.f$0 = zArr;
        this.f$1 = j;
        this.f$2 = j2;
        this.f$3 = numberPicker;
        this.f$4 = numberPicker2;
        this.f$5 = numberPicker3;
        this.f$6 = calendar;
        this.f$7 = scheduleDatePickerDelegate;
        this.f$8 = builder;
    }

    public final void onClick(View view) {
        AlertsCreator.lambda$createScheduleDatePickerDialog$46(this.f$0, this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, this.f$7, this.f$8, view);
    }
}
