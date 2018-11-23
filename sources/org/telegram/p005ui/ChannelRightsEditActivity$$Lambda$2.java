package org.telegram.p005ui;

import android.app.DatePickerDialog.OnDateSetListener;
import android.widget.DatePicker;

/* renamed from: org.telegram.ui.ChannelRightsEditActivity$$Lambda$2 */
final /* synthetic */ class ChannelRightsEditActivity$$Lambda$2 implements OnDateSetListener {
    private final ChannelRightsEditActivity arg$1;

    ChannelRightsEditActivity$$Lambda$2(ChannelRightsEditActivity channelRightsEditActivity) {
        this.arg$1 = channelRightsEditActivity;
    }

    public void onDateSet(DatePicker datePicker, int i, int i2, int i3) {
        this.arg$1.lambda$null$2$ChannelRightsEditActivity(datePicker, i, i2, i3);
    }
}
