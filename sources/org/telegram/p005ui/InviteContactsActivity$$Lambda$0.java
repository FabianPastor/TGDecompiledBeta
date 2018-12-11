package org.telegram.p005ui;

import android.view.View;
import org.telegram.p005ui.Components.RecyclerListView.OnItemClickListener;

/* renamed from: org.telegram.ui.InviteContactsActivity$$Lambda$0 */
final /* synthetic */ class InviteContactsActivity$$Lambda$0 implements OnItemClickListener {
    private final InviteContactsActivity arg$1;

    InviteContactsActivity$$Lambda$0(InviteContactsActivity inviteContactsActivity) {
        this.arg$1 = inviteContactsActivity;
    }

    public void onItemClick(View view, int i) {
        this.arg$1.lambda$createView$0$InviteContactsActivity(view, i);
    }
}
