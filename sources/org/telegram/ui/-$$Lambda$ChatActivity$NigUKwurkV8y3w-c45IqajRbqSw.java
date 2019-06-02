package org.telegram.ui;

import org.telegram.tgnet.TLRPC.User;
import org.telegram.ui.PhonebookSelectActivity.PhonebookSelectActivityDelegate;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$ChatActivity$NigUKwurkV8y3w-CLASSNAMEIqajRbqSw implements PhonebookSelectActivityDelegate {
    private final /* synthetic */ ChatActivity f$0;

    public /* synthetic */ -$$Lambda$ChatActivity$NigUKwurkV8y3w-CLASSNAMEIqajRbqSw(ChatActivity chatActivity) {
        this.f$0 = chatActivity;
    }

    public final void didSelectContact(User user) {
        this.f$0.lambda$processSelectedAttach$43$ChatActivity(user);
    }
}
