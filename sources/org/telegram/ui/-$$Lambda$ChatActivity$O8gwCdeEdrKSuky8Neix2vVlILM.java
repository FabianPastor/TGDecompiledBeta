package org.telegram.ui;

import org.telegram.tgnet.TLRPC.User;
import org.telegram.ui.Components.PhonebookSelectShareAlert.PhonebookShareAlertDelegate;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$ChatActivity$O8gwCdeEdrKSuky8Neix2vVlILM implements PhonebookShareAlertDelegate {
    private final /* synthetic */ ChatActivity f$0;

    public /* synthetic */ -$$Lambda$ChatActivity$O8gwCdeEdrKSuky8Neix2vVlILM(ChatActivity chatActivity) {
        this.f$0 = chatActivity;
    }

    public final void didSelectContact(User user, boolean z, int i) {
        this.f$0.lambda$openContactsSelectActivity$52$ChatActivity(user, z, i);
    }
}
