package org.telegram.ui.Components;

import org.telegram.tgnet.TLRPC.User;
import org.telegram.ui.Components.PhonebookSelectShareAlert.PhonebookShareAlertDelegate;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$PhonebookSelectShareAlert$s8Ge4bVdSAGz3-QOO4upUWstpTA implements PhonebookShareAlertDelegate {
    private final /* synthetic */ PhonebookSelectShareAlert f$0;

    public /* synthetic */ -$$Lambda$PhonebookSelectShareAlert$s8Ge4bVdSAGz3-QOO4upUWstpTA(PhonebookSelectShareAlert phonebookSelectShareAlert) {
        this.f$0 = phonebookSelectShareAlert;
    }

    public final void didSelectContact(User user, boolean z, int i) {
        this.f$0.lambda$null$0$PhonebookSelectShareAlert(user, z, i);
    }
}
