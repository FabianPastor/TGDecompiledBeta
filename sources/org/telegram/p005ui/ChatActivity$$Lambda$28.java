package org.telegram.p005ui;

import android.view.View;
import org.telegram.p005ui.Components.RecyclerListView.OnItemClickListener;

/* renamed from: org.telegram.ui.ChatActivity$$Lambda$28 */
final /* synthetic */ class ChatActivity$$Lambda$28 implements OnItemClickListener {
    private final ChatActivity arg$1;

    ChatActivity$$Lambda$28(ChatActivity chatActivity) {
        this.arg$1 = chatActivity;
    }

    public void onItemClick(View view, int i) {
        this.arg$1.lambda$initStickers$36$ChatActivity(view, i);
    }
}
