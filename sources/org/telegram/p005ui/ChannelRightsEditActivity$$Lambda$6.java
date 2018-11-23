package org.telegram.p005ui;

import android.app.TimePickerDialog.OnTimeSetListener;
import android.widget.TimePicker;

/* renamed from: org.telegram.ui.ChannelRightsEditActivity$$Lambda$6 */
final /* synthetic */ class ChannelRightsEditActivity$$Lambda$6 implements OnTimeSetListener {
    private final ChannelRightsEditActivity arg$1;
    private final int arg$2;

    ChannelRightsEditActivity$$Lambda$6(ChannelRightsEditActivity channelRightsEditActivity, int i) {
        this.arg$1 = channelRightsEditActivity;
        this.arg$2 = i;
    }

    public void onTimeSet(TimePicker timePicker, int i, int i2) {
        this.arg$1.lambda$null$0$ChannelRightsEditActivity(this.arg$2, timePicker, i, i2);
    }
}
