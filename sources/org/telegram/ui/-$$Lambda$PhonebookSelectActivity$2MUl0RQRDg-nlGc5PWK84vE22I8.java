package org.telegram.ui;

import org.telegram.tgnet.TLRPC.User;
import org.telegram.ui.PhonebookSelectActivity.PhonebookSelectActivityDelegate;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$PhonebookSelectActivity$2MUl0RQRDg-nlGc5PWK84vE22I8 implements PhonebookSelectActivityDelegate {
    private final /* synthetic */ PhonebookSelectActivity f$0;

    public /* synthetic */ -$$Lambda$PhonebookSelectActivity$2MUl0RQRDg-nlGc5PWK84vE22I8(PhonebookSelectActivity phonebookSelectActivity) {
        this.f$0 = phonebookSelectActivity;
    }

    public final void didSelectContact(User user, boolean z, int i) {
        this.f$0.lambda$null$0$PhonebookSelectActivity(user, z, i);
    }
}
