package org.telegram.ui;

import android.view.View;
import org.telegram.ui.Components.RecyclerListView.OnItemClickListener;

final /* synthetic */ class ChatActivity$$Lambda$15 implements OnItemClickListener {
    private final ChatActivity arg$1;

    ChatActivity$$Lambda$15(ChatActivity chatActivity) {
        this.arg$1 = chatActivity;
    }

    public void onItemClick(View view, int i) {
        this.arg$1.lambda$createView$17$ChatActivity(view, i);
    }
}
