package org.telegram.p005ui;

import android.view.View;
import org.telegram.p005ui.Components.RecyclerListView.OnItemClickListener;

/* renamed from: org.telegram.ui.BlockedUsersActivity$$Lambda$0 */
final /* synthetic */ class BlockedUsersActivity$$Lambda$0 implements OnItemClickListener {
    private final BlockedUsersActivity arg$1;

    BlockedUsersActivity$$Lambda$0(BlockedUsersActivity blockedUsersActivity) {
        this.arg$1 = blockedUsersActivity;
    }

    public void onItemClick(View view, int i) {
        this.arg$1.lambda$createView$0$BlockedUsersActivity(view, i);
    }
}
