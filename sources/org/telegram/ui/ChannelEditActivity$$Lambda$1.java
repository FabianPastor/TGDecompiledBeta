package org.telegram.ui;

import android.view.View;
import org.telegram.ui.Components.RecyclerListView.OnItemClickListener;

final /* synthetic */ class ChannelEditActivity$$Lambda$1 implements OnItemClickListener {
    private final ChannelEditActivity arg$1;

    ChannelEditActivity$$Lambda$1(ChannelEditActivity channelEditActivity) {
        this.arg$1 = channelEditActivity;
    }

    public void onItemClick(View view, int i) {
        this.arg$1.lambda$createView$1$ChannelEditActivity(view, i);
    }
}
