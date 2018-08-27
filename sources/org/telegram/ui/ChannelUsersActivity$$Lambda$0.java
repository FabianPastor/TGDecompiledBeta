package org.telegram.ui;

import android.view.View;
import org.telegram.ui.Components.RecyclerListView.OnItemClickListener;

final /* synthetic */ class ChannelUsersActivity$$Lambda$0 implements OnItemClickListener {
    private final ChannelUsersActivity arg$1;

    ChannelUsersActivity$$Lambda$0(ChannelUsersActivity channelUsersActivity) {
        this.arg$1 = channelUsersActivity;
    }

    public void onItemClick(View view, int i) {
        this.arg$1.lambda$createView$3$ChannelUsersActivity(view, i);
    }
}
