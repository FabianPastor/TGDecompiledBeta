package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

/* renamed from: org.telegram.messenger.-$$Lambda$MessagesController$vY8LSvCjnIQDZHj1Uo3ljfBiaZk  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$MessagesController$vY8LSvCjnIQDZHj1Uo3ljfBiaZk implements RequestDelegate {
    public static final /* synthetic */ $$Lambda$MessagesController$vY8LSvCjnIQDZHj1Uo3ljfBiaZk INSTANCE = new $$Lambda$MessagesController$vY8LSvCjnIQDZHj1Uo3ljfBiaZk();

    private /* synthetic */ $$Lambda$MessagesController$vY8LSvCjnIQDZHj1Uo3ljfBiaZk() {
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        MessagesController.lambda$markMentionsAsRead$177(tLObject, tLRPC$TL_error);
    }
}
