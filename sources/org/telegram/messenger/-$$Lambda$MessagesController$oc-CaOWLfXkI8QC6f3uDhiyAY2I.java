package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$MessagesController$oc-CaOWLfXkI8QC6f3uDhiyAY2I implements RequestDelegate {
    private final /* synthetic */ MessagesController f$0;

    public /* synthetic */ -$$Lambda$MessagesController$oc-CaOWLfXkI8QC6f3uDhiyAY2I(MessagesController messagesController) {
        this.f$0 = messagesController;
    }

    public final void run(TLObject tLObject, TL_error tL_error) {
        this.f$0.lambda$markMentionMessageAsRead$158$MessagesController(tLObject, tL_error);
    }
}
