package org.telegram.ui;

import java.util.ArrayList;
import org.telegram.ui.InviteContactsActivity.InviteAdapter;

final /* synthetic */ class InviteContactsActivity$InviteAdapter$$Lambda$0 implements Runnable {
    private final InviteAdapter arg$1;
    private final ArrayList arg$2;
    private final ArrayList arg$3;

    InviteContactsActivity$InviteAdapter$$Lambda$0(InviteAdapter inviteAdapter, ArrayList arrayList, ArrayList arrayList2) {
        this.arg$1 = inviteAdapter;
        this.arg$2 = arrayList;
        this.arg$3 = arrayList2;
    }

    public void run() {
        this.arg$1.lambda$updateSearchResults$0$InviteContactsActivity$InviteAdapter(this.arg$2, this.arg$3);
    }
}
