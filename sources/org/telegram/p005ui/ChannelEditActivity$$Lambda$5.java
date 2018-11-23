package org.telegram.p005ui;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;

/* renamed from: org.telegram.ui.ChannelEditActivity$$Lambda$5 */
final /* synthetic */ class ChannelEditActivity$$Lambda$5 implements OnClickListener {
    private final ChannelEditActivity arg$1;
    private final Context arg$2;

    ChannelEditActivity$$Lambda$5(ChannelEditActivity channelEditActivity, Context context) {
        this.arg$1 = channelEditActivity;
        this.arg$2 = context;
    }

    public void onClick(View view) {
        this.arg$1.lambda$createView$7$ChannelEditActivity(this.arg$2, view);
    }
}
