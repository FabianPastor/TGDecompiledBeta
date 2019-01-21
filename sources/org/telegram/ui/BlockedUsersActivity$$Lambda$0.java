package org.telegram.ui;

import android.view.View;
import org.telegram.ui.Components.RecyclerListView.OnItemClickListener;

final /* synthetic */ class BlockedUsersActivity$$Lambda$0 implements OnItemClickListener {
    private final BlockedUsersActivity arg$1;

    BlockedUsersActivity$$Lambda$0(BlockedUsersActivity blockedUsersActivity) {
        this.arg$1 = blockedUsersActivity;
    }

    public void onItemClick(View view, int i) {
        this.arg$1.lambda$createView$0$BlockedUsersActivity(view, i);
    }
}
