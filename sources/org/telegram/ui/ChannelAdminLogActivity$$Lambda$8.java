package org.telegram.ui;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;

final /* synthetic */ class ChannelAdminLogActivity$$Lambda$8 implements OnClickListener {
    private final ChannelAdminLogActivity arg$1;
    private final String arg$2;

    ChannelAdminLogActivity$$Lambda$8(ChannelAdminLogActivity channelAdminLogActivity, String str) {
        this.arg$1 = channelAdminLogActivity;
        this.arg$2 = str;
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        this.arg$1.lambda$showOpenUrlAlert$14$ChannelAdminLogActivity(this.arg$2, dialogInterface, i);
    }
}
