package org.telegram.ui;

import org.telegram.tgnet.TLRPC.User;
import org.telegram.ui.PhonebookSelectActivity.PhonebookSelectActivityDelegate;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$ChatActivity$ECCBG_hSYesankJFk1URpYX6d8c implements PhonebookSelectActivityDelegate {
    private final /* synthetic */ ChatActivity f$0;

    public /* synthetic */ -$$Lambda$ChatActivity$ECCBG_hSYesankJFk1URpYX6d8c(ChatActivity chatActivity) {
        this.f$0 = chatActivity;
    }

    public final void didSelectContact(User user, boolean z, int i) {
        this.f$0.lambda$processSelectedAttach$49$ChatActivity(user, z, i);
    }
}
