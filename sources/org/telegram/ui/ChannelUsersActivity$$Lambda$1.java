package org.telegram.ui;

import android.view.View;
import org.telegram.ui.Components.RecyclerListView.OnItemLongClickListener;

final /* synthetic */ class ChannelUsersActivity$$Lambda$1 implements OnItemLongClickListener {
    private final ChannelUsersActivity arg$1;

    ChannelUsersActivity$$Lambda$1(ChannelUsersActivity channelUsersActivity) {
        this.arg$1 = channelUsersActivity;
    }

    public boolean onItemClick(View view, int i) {
        return this.arg$1.lambda$createView$4$ChannelUsersActivity(view, i);
    }
}
