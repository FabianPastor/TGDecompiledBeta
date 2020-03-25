package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

/* renamed from: org.telegram.messenger.-$$Lambda$MessagesController$egPYFUimpyOBu-dz6sAxJBwWsAQ  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$MessagesController$egPYFUimpyOBudz6sAxJBwWsAQ implements RequestDelegate {
    public static final /* synthetic */ $$Lambda$MessagesController$egPYFUimpyOBudz6sAxJBwWsAQ INSTANCE = new $$Lambda$MessagesController$egPYFUimpyOBudz6sAxJBwWsAQ();

    private /* synthetic */ $$Lambda$MessagesController$egPYFUimpyOBudz6sAxJBwWsAQ() {
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        MessagesController.lambda$unblockUser$73(tLObject, tLRPC$TL_error);
    }
}
