package org.telegram.p005ui;

import android.view.View;
import org.telegram.p005ui.Components.RecyclerListView.OnItemLongClickListener;

/* renamed from: org.telegram.ui.ChannelEditActivity$$Lambda$2 */
final /* synthetic */ class ChannelEditActivity$$Lambda$2 implements OnItemLongClickListener {
    private final ChannelEditActivity arg$1;

    ChannelEditActivity$$Lambda$2(ChannelEditActivity channelEditActivity) {
        this.arg$1 = channelEditActivity;
    }

    public boolean onItemClick(View view, int i) {
        return this.arg$1.lambda$createView$2$ChannelEditActivity(view, i);
    }
}
