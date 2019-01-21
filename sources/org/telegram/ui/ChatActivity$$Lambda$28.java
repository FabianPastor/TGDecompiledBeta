package org.telegram.ui;

import android.view.View;
import org.telegram.ui.Components.RecyclerListView.OnItemClickListener;

final /* synthetic */ class ChatActivity$$Lambda$28 implements OnItemClickListener {
    private final ChatActivity arg$1;

    ChatActivity$$Lambda$28(ChatActivity chatActivity) {
        this.arg$1 = chatActivity;
    }

    public void onItemClick(View view, int i) {
        this.arg$1.lambda$initStickers$36$ChatActivity(view, i);
    }
}
