package org.telegram.ui;

import android.view.View;
import org.telegram.ui.Components.RecyclerListView.OnItemClickListener;

final /* synthetic */ class InviteContactsActivity$$Lambda$0 implements OnItemClickListener {
    private final InviteContactsActivity arg$1;

    InviteContactsActivity$$Lambda$0(InviteContactsActivity inviteContactsActivity) {
        this.arg$1 = inviteContactsActivity;
    }

    public void onItemClick(View view, int i) {
        this.arg$1.lambda$createView$0$InviteContactsActivity(view, i);
    }
}
