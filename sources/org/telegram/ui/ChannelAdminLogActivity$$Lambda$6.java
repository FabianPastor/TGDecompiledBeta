package org.telegram.ui;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import java.util.ArrayList;

final /* synthetic */ class ChannelAdminLogActivity$$Lambda$6 implements OnClickListener {
    private final ChannelAdminLogActivity arg$1;
    private final ArrayList arg$2;

    ChannelAdminLogActivity$$Lambda$6(ChannelAdminLogActivity channelAdminLogActivity, ArrayList arrayList) {
        this.arg$1 = channelAdminLogActivity;
        this.arg$2 = arrayList;
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        this.arg$1.lambda$createMenu$11$ChannelAdminLogActivity(this.arg$2, dialogInterface, i);
    }
}
