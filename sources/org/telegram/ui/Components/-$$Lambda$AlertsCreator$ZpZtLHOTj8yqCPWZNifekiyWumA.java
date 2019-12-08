package org.telegram.ui.Components;

import android.view.View;
import android.view.View.OnClickListener;
import java.util.Calendar;
import org.telegram.ui.ActionBar.BottomSheet.Builder;
import org.telegram.ui.Components.AlertsCreator.ScheduleDatePickerDelegate;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$AlertsCreator$ZpZtLHOTj8yqCPWZNifekiyWumA implements OnClickListener {
    private final /* synthetic */ boolean[] f$0;
    private final /* synthetic */ int f$1;
    private final /* synthetic */ long f$2;
    private final /* synthetic */ NumberPicker f$3;
    private final /* synthetic */ NumberPicker f$4;
    private final /* synthetic */ NumberPicker f$5;
    private final /* synthetic */ Calendar f$6;
    private final /* synthetic */ ScheduleDatePickerDelegate f$7;
    private final /* synthetic */ Builder f$8;

    public /* synthetic */ -$$Lambda$AlertsCreator$ZpZtLHOTj8yqCPWZNifekiyWumA(boolean[] zArr, int i, long j, NumberPicker numberPicker, NumberPicker numberPicker2, NumberPicker numberPicker3, Calendar calendar, ScheduleDatePickerDelegate scheduleDatePickerDelegate, Builder builder) {
        this.f$0 = zArr;
        this.f$1 = i;
        this.f$2 = j;
        this.f$3 = numberPicker;
        this.f$4 = numberPicker2;
        this.f$5 = numberPicker3;
        this.f$6 = calendar;
        this.f$7 = scheduleDatePickerDelegate;
        this.f$8 = builder;
    }

    public final void onClick(View view) {
        AlertsCreator.lambda$createScheduleDatePickerDialog$28(this.f$0, this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, this.f$7, this.f$8, view);
    }
}
