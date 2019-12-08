package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$MessagesController$0K2PJ7lNbgyyKB5A5U-3gRcukdg implements RequestDelegate {
    public static final /* synthetic */ -$$Lambda$MessagesController$0K2PJ7lNbgyyKB5A5U-3gRcukdg INSTANCE = new -$$Lambda$MessagesController$0K2PJ7lNbgyyKB5A5U-3gRcukdg();

    private /* synthetic */ -$$Lambda$MessagesController$0K2PJ7lNbgyyKB5A5U-3gRcukdg() {
    }

    public final void run(TLObject tLObject, TL_error tL_error) {
        MessagesController.lambda$markMentionsAsRead$153(tLObject, tL_error);
    }
}
