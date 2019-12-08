package org.telegram.ui;

import org.telegram.tgnet.TLRPC.User;
import org.telegram.ui.PhonebookSelectActivity.PhonebookSelectActivityDelegate;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$ChatActivity$RAA1X6-mOF1TJmg5TeTD_KptPpI implements PhonebookSelectActivityDelegate {
    private final /* synthetic */ ChatActivity f$0;

    public /* synthetic */ -$$Lambda$ChatActivity$RAA1X6-mOF1TJmg5TeTD_KptPpI(ChatActivity chatActivity) {
        this.f$0 = chatActivity;
    }

    public final void didSelectContact(User user, boolean z, int i) {
        this.f$0.lambda$processSelectedAttach$51$ChatActivity(user, z, i);
    }
}
