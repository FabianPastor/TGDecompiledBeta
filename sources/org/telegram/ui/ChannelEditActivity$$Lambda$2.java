package org.telegram.ui;

import android.view.View;
import org.telegram.ui.Components.RecyclerListView.OnItemLongClickListener;

final /* synthetic */ class ChannelEditActivity$$Lambda$2 implements OnItemLongClickListener {
    private final ChannelEditActivity arg$1;

    ChannelEditActivity$$Lambda$2(ChannelEditActivity channelEditActivity) {
        this.arg$1 = channelEditActivity;
    }

    public boolean onItemClick(View view, int i) {
        return this.arg$1.lambda$createView$2$ChannelEditActivity(view, i);
    }
}
