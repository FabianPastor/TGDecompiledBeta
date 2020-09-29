package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

/* renamed from: org.telegram.messenger.-$$Lambda$MessagesController$RAqb2cTX3j930MsIeO-9HWbZk5M  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$MessagesController$RAqb2cTX3j930MsIeO9HWbZk5M implements RequestDelegate {
    public static final /* synthetic */ $$Lambda$MessagesController$RAqb2cTX3j930MsIeO9HWbZk5M INSTANCE = new $$Lambda$MessagesController$RAqb2cTX3j930MsIeO9HWbZk5M();

    private /* synthetic */ $$Lambda$MessagesController$RAqb2cTX3j930MsIeO9HWbZk5M() {
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        MessagesController.lambda$unblockPeer$74(tLObject, tLRPC$TL_error);
    }
}
