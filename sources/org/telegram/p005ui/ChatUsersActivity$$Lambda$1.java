package org.telegram.p005ui;

import android.view.View;
import org.telegram.p005ui.Components.RecyclerListView.OnItemLongClickListener;

/* renamed from: org.telegram.ui.ChatUsersActivity$$Lambda$1 */
final /* synthetic */ class ChatUsersActivity$$Lambda$1 implements OnItemLongClickListener {
    private final ChatUsersActivity arg$1;

    ChatUsersActivity$$Lambda$1(ChatUsersActivity chatUsersActivity) {
        this.arg$1 = chatUsersActivity;
    }

    public boolean onItemClick(View view, int i) {
        return this.arg$1.lambda$createView$1$ChatUsersActivity(view, i);
    }
}
