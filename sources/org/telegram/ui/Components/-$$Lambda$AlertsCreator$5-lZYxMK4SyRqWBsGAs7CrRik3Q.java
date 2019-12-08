package org.telegram.ui.Components;

import android.view.View;
import android.view.View.OnClickListener;
import java.util.Calendar;
import org.telegram.ui.ActionBar.BottomSheet.Builder;
import org.telegram.ui.Components.AlertsCreator.ScheduleDatePickerDelegate;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$AlertsCreator$5-lZYxMK4SyRqWBsGAs7CrRik3Q implements OnClickListener {
    private final /* synthetic */ boolean[] f$0;
    private final /* synthetic */ boolean f$1;
    private final /* synthetic */ NumberPicker f$2;
    private final /* synthetic */ NumberPicker f$3;
    private final /* synthetic */ NumberPicker f$4;
    private final /* synthetic */ Calendar f$5;
    private final /* synthetic */ ScheduleDatePickerDelegate f$6;
    private final /* synthetic */ Builder f$7;

    public /* synthetic */ -$$Lambda$AlertsCreator$5-lZYxMK4SyRqWBsGAs7CrRik3Q(boolean[] zArr, boolean z, NumberPicker numberPicker, NumberPicker numberPicker2, NumberPicker numberPicker3, Calendar calendar, ScheduleDatePickerDelegate scheduleDatePickerDelegate, Builder builder) {
        this.f$0 = zArr;
        this.f$1 = z;
        this.f$2 = numberPicker;
        this.f$3 = numberPicker2;
        this.f$4 = numberPicker3;
        this.f$5 = calendar;
        this.f$6 = scheduleDatePickerDelegate;
        this.f$7 = builder;
    }

    public final void onClick(View view) {
        AlertsCreator.lambda$createScheduleDatePickerDialog$26(this.f$0, this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, this.f$7, view);
    }
}
