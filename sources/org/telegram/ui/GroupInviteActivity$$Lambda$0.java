package org.telegram.ui;

import android.view.View;
import org.telegram.ui.Components.RecyclerListView.OnItemClickListener;

final /* synthetic */ class GroupInviteActivity$$Lambda$0 implements OnItemClickListener {
    private final GroupInviteActivity arg$1;

    GroupInviteActivity$$Lambda$0(GroupInviteActivity groupInviteActivity) {
        this.arg$1 = groupInviteActivity;
    }

    public void onItemClick(View view, int i) {
        this.arg$1.lambda$createView$1$GroupInviteActivity(view, i);
    }
}
