package org.telegram.ui;

import android.view.View;
import org.telegram.ui.Components.RecyclerListView.OnItemLongClickListener;

final /* synthetic */ class ChatUsersActivity$$Lambda$1 implements OnItemLongClickListener {
    private final ChatUsersActivity arg$1;

    ChatUsersActivity$$Lambda$1(ChatUsersActivity chatUsersActivity) {
        this.arg$1 = chatUsersActivity;
    }

    public boolean onItemClick(View view, int i) {
        return this.arg$1.lambda$createView$1$ChatUsersActivity(view, i);
    }
}
