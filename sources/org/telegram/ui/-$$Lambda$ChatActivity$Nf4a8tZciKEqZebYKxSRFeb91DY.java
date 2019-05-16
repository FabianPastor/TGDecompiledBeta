package org.telegram.ui;

import org.telegram.tgnet.TLRPC.User;
import org.telegram.ui.PhonebookSelectActivity.PhonebookSelectActivityDelegate;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$ChatActivity$Nf4a8tZciKEqZebYKxSRFeb91DY implements PhonebookSelectActivityDelegate {
    private final /* synthetic */ ChatActivity f$0;

    public /* synthetic */ -$$Lambda$ChatActivity$Nf4a8tZciKEqZebYKxSRFeb91DY(ChatActivity chatActivity) {
        this.f$0 = chatActivity;
    }

    public final void didSelectContact(User user) {
        this.f$0.lambda$processSelectedAttach$42$ChatActivity(user);
    }
}
