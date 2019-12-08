package org.telegram.ui;

import org.telegram.tgnet.TLRPC.User;
import org.telegram.ui.Components.PhonebookSelectShareAlert.PhonebookShareAlertDelegate;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$ChatActivity$4DvO9LxOvJ0FxqSlS6eBPWr5fgc implements PhonebookShareAlertDelegate {
    private final /* synthetic */ ChatActivity f$0;

    public /* synthetic */ -$$Lambda$ChatActivity$4DvO9LxOvJ0FxqSlS6eBPWr5fgc(ChatActivity chatActivity) {
        this.f$0 = chatActivity;
    }

    public final void didSelectContact(User user, boolean z, int i) {
        this.f$0.lambda$openContactsSelectActivity$51$ChatActivity(user, z, i);
    }
}
