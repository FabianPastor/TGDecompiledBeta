package org.telegram.ui.Components;

import android.view.View;
import org.telegram.ui.ActionBar.BottomSheet;
import org.telegram.ui.Components.AlertsCreator;

public final /* synthetic */ class AlertsCreator$$ExternalSyntheticLambda63 implements View.OnClickListener {
    public final /* synthetic */ int[] f$0;
    public final /* synthetic */ NumberPicker f$1;
    public final /* synthetic */ AlertsCreator.ScheduleDatePickerDelegate f$2;
    public final /* synthetic */ BottomSheet.Builder f$3;

    public /* synthetic */ AlertsCreator$$ExternalSyntheticLambda63(int[] iArr, NumberPicker numberPicker, AlertsCreator.ScheduleDatePickerDelegate scheduleDatePickerDelegate, BottomSheet.Builder builder) {
        this.f$0 = iArr;
        this.f$1 = numberPicker;
        this.f$2 = scheduleDatePickerDelegate;
        this.f$3 = builder;
    }

    public final void onClick(View view) {
        AlertsCreator.lambda$createAutoDeleteDatePickerDialog$65(this.f$0, this.f$1, this.f$2, this.f$3, view);
    }
}
