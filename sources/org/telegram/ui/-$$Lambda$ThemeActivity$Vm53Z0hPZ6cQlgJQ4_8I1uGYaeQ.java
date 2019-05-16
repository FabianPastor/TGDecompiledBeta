package org.telegram.ui;

import android.app.TimePickerDialog.OnTimeSetListener;
import android.widget.TimePicker;
import org.telegram.ui.Cells.TextSettingsCell;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$ThemeActivity$Vm53Z0hPZ6cQlgJQ4_8I1uGYaeQ implements OnTimeSetListener {
    private final /* synthetic */ ThemeActivity f$0;
    private final /* synthetic */ int f$1;
    private final /* synthetic */ TextSettingsCell f$2;

    public /* synthetic */ -$$Lambda$ThemeActivity$Vm53Z0hPZ6cQlgJQ4_8I1uGYaeQ(ThemeActivity themeActivity, int i, TextSettingsCell textSettingsCell) {
        this.f$0 = themeActivity;
        this.f$1 = i;
        this.f$2 = textSettingsCell;
    }

    public final void onTimeSet(TimePicker timePicker, int i, int i2) {
        this.f$0.lambda$null$3$ThemeActivity(this.f$1, this.f$2, timePicker, i, i2);
    }
}
