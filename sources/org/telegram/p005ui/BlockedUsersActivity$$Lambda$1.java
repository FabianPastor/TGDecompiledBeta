package org.telegram.p005ui;

import android.view.View;
import org.telegram.p005ui.Components.RecyclerListView.OnItemLongClickListener;

/* renamed from: org.telegram.ui.BlockedUsersActivity$$Lambda$1 */
final /* synthetic */ class BlockedUsersActivity$$Lambda$1 implements OnItemLongClickListener {
    private final BlockedUsersActivity arg$1;

    BlockedUsersActivity$$Lambda$1(BlockedUsersActivity blockedUsersActivity) {
        this.arg$1 = blockedUsersActivity;
    }

    public boolean onItemClick(View view, int i) {
        return this.arg$1.lambda$createView$2$BlockedUsersActivity(view, i);
    }
}
