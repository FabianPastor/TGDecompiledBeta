package org.telegram.p005ui;

import android.app.TimePickerDialog.OnTimeSetListener;
import android.widget.TimePicker;
import org.telegram.p005ui.Cells.TextSettingsCell;

/* renamed from: org.telegram.ui.ThemeActivity$$Lambda$9 */
final /* synthetic */ class ThemeActivity$$Lambda$9 implements OnTimeSetListener {
    private final ThemeActivity arg$1;
    private final int arg$2;
    private final TextSettingsCell arg$3;

    ThemeActivity$$Lambda$9(ThemeActivity themeActivity, int i, TextSettingsCell textSettingsCell) {
        this.arg$1 = themeActivity;
        this.arg$2 = i;
        this.arg$3 = textSettingsCell;
    }

    public void onTimeSet(TimePicker timePicker, int i, int i2) {
        this.arg$1.lambda$null$5$ThemeActivity(this.arg$2, this.arg$3, timePicker, i, i2);
    }
}
