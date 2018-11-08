package org.telegram.p005ui;

import android.view.View;
import org.telegram.p005ui.Components.RecyclerListView.OnItemClickListener;

/* renamed from: org.telegram.ui.ChatActivity$$Lambda$15 */
final /* synthetic */ class ChatActivity$$Lambda$15 implements OnItemClickListener {
    private final ChatActivity arg$1;

    ChatActivity$$Lambda$15(ChatActivity chatActivity) {
        this.arg$1 = chatActivity;
    }

    public void onItemClick(View view, int i) {
        this.arg$1.lambda$createView$17$ChatActivity(view, i);
    }
}
