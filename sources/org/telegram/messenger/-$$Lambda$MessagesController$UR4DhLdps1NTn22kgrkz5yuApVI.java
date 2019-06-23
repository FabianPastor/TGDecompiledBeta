package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$MessagesController$UR4DhLdps1NTn22kgrkz5yuApVI implements RequestDelegate {
    public static final /* synthetic */ -$$Lambda$MessagesController$UR4DhLdps1NTn22kgrkz5yuApVI INSTANCE = new -$$Lambda$MessagesController$UR4DhLdps1NTn22kgrkz5yuApVI();

    private /* synthetic */ -$$Lambda$MessagesController$UR4DhLdps1NTn22kgrkz5yuApVI() {
    }

    public final void run(TLObject tLObject, TL_error tL_error) {
        MessagesController.lambda$processUpdates$238(tLObject, tL_error);
    }
}
