package org.telegram.ui.Components;

import android.view.View;
import java.util.Calendar;
import org.telegram.ui.ActionBar.BottomSheet;
import org.telegram.ui.Components.AlertsCreator;

public final /* synthetic */ class AlertsCreator$$ExternalSyntheticLambda45 implements View.OnClickListener {
    public final /* synthetic */ NumberPicker f$0;
    public final /* synthetic */ NumberPicker f$1;
    public final /* synthetic */ NumberPicker f$2;
    public final /* synthetic */ Calendar f$3;
    public final /* synthetic */ AlertsCreator.ScheduleDatePickerDelegate f$4;
    public final /* synthetic */ BottomSheet.Builder f$5;

    public /* synthetic */ AlertsCreator$$ExternalSyntheticLambda45(NumberPicker numberPicker, NumberPicker numberPicker2, NumberPicker numberPicker3, Calendar calendar, AlertsCreator.ScheduleDatePickerDelegate scheduleDatePickerDelegate, BottomSheet.Builder builder) {
        this.f$0 = numberPicker;
        this.f$1 = numberPicker2;
        this.f$2 = numberPicker3;
        this.f$3 = calendar;
        this.f$4 = scheduleDatePickerDelegate;
        this.f$5 = builder;
    }

    public final void onClick(View view) {
        AlertsCreator.lambda$createDatePickerDialog$53(this.f$0, this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, view);
    }
}
