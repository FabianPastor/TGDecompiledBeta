package org.telegram.ui;

import android.view.View;
import org.telegram.ui.Components.RecyclerListView.OnItemLongClickListener;

final /* synthetic */ class ChatActivity$$Lambda$16 implements OnItemLongClickListener {
    private final ChatActivity arg$1;

    ChatActivity$$Lambda$16(ChatActivity chatActivity) {
        this.arg$1 = chatActivity;
    }

    public boolean onItemClick(View view, int i) {
        return this.arg$1.lambda$createView$19$ChatActivity(view, i);
    }
}
