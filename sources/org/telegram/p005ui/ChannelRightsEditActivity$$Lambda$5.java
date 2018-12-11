package org.telegram.p005ui;

import android.content.DialogInterface;
import android.content.DialogInterface.OnShowListener;
import android.widget.DatePicker;

/* renamed from: org.telegram.ui.ChannelRightsEditActivity$$Lambda$5 */
final /* synthetic */ class ChannelRightsEditActivity$$Lambda$5 implements OnShowListener {
    private final DatePicker arg$1;

    ChannelRightsEditActivity$$Lambda$5(DatePicker datePicker) {
        this.arg$1 = datePicker;
    }

    public void onShow(DialogInterface dialogInterface) {
        ChannelRightsEditActivity.lambda$null$5$ChannelRightsEditActivity(this.arg$1, dialogInterface);
    }
}
