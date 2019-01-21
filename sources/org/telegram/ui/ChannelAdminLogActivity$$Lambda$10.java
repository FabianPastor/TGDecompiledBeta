package org.telegram.ui;

import android.app.DatePickerDialog.OnDateSetListener;
import android.widget.DatePicker;

final /* synthetic */ class ChannelAdminLogActivity$$Lambda$10 implements OnDateSetListener {
    private final ChannelAdminLogActivity arg$1;

    ChannelAdminLogActivity$$Lambda$10(ChannelAdminLogActivity channelAdminLogActivity) {
        this.arg$1 = channelAdminLogActivity;
    }

    public void onDateSet(DatePicker datePicker, int i, int i2, int i3) {
        this.arg$1.lambda$null$7$ChannelAdminLogActivity(datePicker, i, i2, i3);
    }
}
