package org.telegram.ui;

import android.view.View;
import org.telegram.ui.Components.RecyclerListView.OnItemLongClickListener;

final /* synthetic */ class BlockedUsersActivity$$Lambda$1 implements OnItemLongClickListener {
    private final BlockedUsersActivity arg$1;

    BlockedUsersActivity$$Lambda$1(BlockedUsersActivity blockedUsersActivity) {
        this.arg$1 = blockedUsersActivity;
    }

    public boolean onItemClick(View view, int i) {
        return this.arg$1.lambda$createView$2$BlockedUsersActivity(view, i);
    }
}
