package org.telegram.p005ui;

import android.view.View;
import org.telegram.p005ui.Components.RecyclerListView.OnItemClickListener;

/* renamed from: org.telegram.ui.ChatUsersActivity$$Lambda$0 */
final /* synthetic */ class ChatUsersActivity$$Lambda$0 implements OnItemClickListener {
    private final ChatUsersActivity arg$1;

    ChatUsersActivity$$Lambda$0(ChatUsersActivity chatUsersActivity) {
        this.arg$1 = chatUsersActivity;
    }

    public void onItemClick(View view, int i) {
        this.arg$1.lambda$createView$0$ChatUsersActivity(view, i);
    }
}
