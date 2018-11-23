package org.telegram.p005ui;

import android.view.View;
import org.telegram.p005ui.Components.RecyclerListView.OnItemClickListener;

/* renamed from: org.telegram.ui.ChannelRightsEditActivity$$Lambda$0 */
final /* synthetic */ class ChannelRightsEditActivity$$Lambda$0 implements OnItemClickListener {
    private final ChannelRightsEditActivity arg$1;

    ChannelRightsEditActivity$$Lambda$0(ChannelRightsEditActivity channelRightsEditActivity) {
        this.arg$1 = channelRightsEditActivity;
    }

    public void onItemClick(View view, int i) {
        this.arg$1.lambda$createView$6$ChannelRightsEditActivity(view, i);
    }
}
