package org.telegram.ui;

import android.app.TimePickerDialog;
import android.widget.TimePicker;
import org.telegram.ui.Cells.TextSettingsCell;

public final /* synthetic */ class ThemeActivity$$ExternalSyntheticLambda0 implements TimePickerDialog.OnTimeSetListener {
    public final /* synthetic */ ThemeActivity f$0;
    public final /* synthetic */ int f$1;
    public final /* synthetic */ TextSettingsCell f$2;

    public /* synthetic */ ThemeActivity$$ExternalSyntheticLambda0(ThemeActivity themeActivity, int i, TextSettingsCell textSettingsCell) {
        this.f$0 = themeActivity;
        this.f$1 = i;
        this.f$2 = textSettingsCell;
    }

    public final void onTimeSet(TimePicker timePicker, int i, int i2) {
        this.f$0.lambda$createView$4(this.f$1, this.f$2, timePicker, i, i2);
    }
}
