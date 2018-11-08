package org.telegram.p005ui;

import android.view.View;
import org.telegram.p005ui.Components.RecyclerListView.OnItemClickListener;

/* renamed from: org.telegram.ui.ChannelUsersActivity$$Lambda$0 */
final /* synthetic */ class ChannelUsersActivity$$Lambda$0 implements OnItemClickListener {
    private final ChannelUsersActivity arg$1;

    ChannelUsersActivity$$Lambda$0(ChannelUsersActivity channelUsersActivity) {
        this.arg$1 = channelUsersActivity;
    }

    public void onItemClick(View view, int i) {
        this.arg$1.lambda$createView$3$ChannelUsersActivity(view, i);
    }
}
