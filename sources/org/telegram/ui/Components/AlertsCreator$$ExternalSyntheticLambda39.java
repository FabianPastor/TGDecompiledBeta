package org.telegram.ui.Components;

import android.view.View;
import java.util.Calendar;
import org.telegram.messenger.MessagesStorage;
import org.telegram.ui.ActionBar.BottomSheet;

public final /* synthetic */ class AlertsCreator$$ExternalSyntheticLambda39 implements View.OnClickListener {
    public final /* synthetic */ long f$0;
    public final /* synthetic */ NumberPicker f$1;
    public final /* synthetic */ NumberPicker f$2;
    public final /* synthetic */ NumberPicker f$3;
    public final /* synthetic */ Calendar f$4;
    public final /* synthetic */ MessagesStorage.IntCallback f$5;
    public final /* synthetic */ BottomSheet.Builder f$6;

    public /* synthetic */ AlertsCreator$$ExternalSyntheticLambda39(long j, NumberPicker numberPicker, NumberPicker numberPicker2, NumberPicker numberPicker3, Calendar calendar, MessagesStorage.IntCallback intCallback, BottomSheet.Builder builder) {
        this.f$0 = j;
        this.f$1 = numberPicker;
        this.f$2 = numberPicker2;
        this.f$3 = numberPicker3;
        this.f$4 = calendar;
        this.f$5 = intCallback;
        this.f$6 = builder;
    }

    public final void onClick(View view) {
        AlertsCreator.lambda$createCalendarPickerDialog$60(this.f$0, this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, view);
    }
}
