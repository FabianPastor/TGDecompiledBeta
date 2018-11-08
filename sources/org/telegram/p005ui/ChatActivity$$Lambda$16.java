package org.telegram.p005ui;

import android.view.View;
import org.telegram.p005ui.Components.RecyclerListView.OnItemLongClickListener;

/* renamed from: org.telegram.ui.ChatActivity$$Lambda$16 */
final /* synthetic */ class ChatActivity$$Lambda$16 implements OnItemLongClickListener {
    private final ChatActivity arg$1;

    ChatActivity$$Lambda$16(ChatActivity chatActivity) {
        this.arg$1 = chatActivity;
    }

    public boolean onItemClick(View view, int i) {
        return this.arg$1.lambda$createView$19$ChatActivity(view, i);
    }
}
