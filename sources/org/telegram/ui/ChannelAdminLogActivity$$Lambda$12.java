package org.telegram.ui;

import android.content.DialogInterface;
import android.content.DialogInterface.OnShowListener;
import android.widget.DatePicker;

final /* synthetic */ class ChannelAdminLogActivity$$Lambda$12 implements OnShowListener {
    private final DatePicker arg$1;

    ChannelAdminLogActivity$$Lambda$12(DatePicker datePicker) {
        this.arg$1 = datePicker;
    }

    public void onShow(DialogInterface dialogInterface) {
        ChannelAdminLogActivity.lambda$null$9$ChannelAdminLogActivity(this.arg$1, dialogInterface);
    }
}
