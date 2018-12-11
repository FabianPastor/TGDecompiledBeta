package org.telegram.p005ui;

import android.view.View;
import org.telegram.p005ui.Components.RecyclerListView.OnItemClickListener;

/* renamed from: org.telegram.ui.ChannelAdminLogActivity$$Lambda$2 */
final /* synthetic */ class ChannelAdminLogActivity$$Lambda$2 implements OnItemClickListener {
    private final ChannelAdminLogActivity arg$1;

    ChannelAdminLogActivity$$Lambda$2(ChannelAdminLogActivity channelAdminLogActivity) {
        this.arg$1 = channelAdminLogActivity;
    }

    public void onItemClick(View view, int i) {
        this.arg$1.lambda$createView$3$ChannelAdminLogActivity(view, i);
    }
}
