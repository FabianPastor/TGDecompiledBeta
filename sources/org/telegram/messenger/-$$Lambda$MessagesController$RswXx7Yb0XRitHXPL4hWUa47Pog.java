package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$MessagesController$RswXx7Yb0XRitHXPL4hWUa47Pog implements RequestDelegate {
    public static final /* synthetic */ -$$Lambda$MessagesController$RswXx7Yb0XRitHXPL4hWUa47Pog INSTANCE = new -$$Lambda$MessagesController$RswXx7Yb0XRitHXPL4hWUa47Pog();

    private /* synthetic */ -$$Lambda$MessagesController$RswXx7Yb0XRitHXPL4hWUa47Pog() {
    }

    public final void run(TLObject tLObject, TL_error tL_error) {
        MessagesController.lambda$processUpdates$253(tLObject, tL_error);
    }
}
