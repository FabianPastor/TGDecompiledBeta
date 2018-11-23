package org.telegram.p005ui;

import android.view.View;
import org.telegram.p005ui.Components.RecyclerListView.OnItemClickListener;

/* renamed from: org.telegram.ui.GroupInviteActivity$$Lambda$0 */
final /* synthetic */ class GroupInviteActivity$$Lambda$0 implements OnItemClickListener {
    private final GroupInviteActivity arg$1;

    GroupInviteActivity$$Lambda$0(GroupInviteActivity groupInviteActivity) {
        this.arg$1 = groupInviteActivity;
    }

    public void onItemClick(View view, int i) {
        this.arg$1.lambda$createView$1$GroupInviteActivity(view, i);
    }
}
