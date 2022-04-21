package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class MessagesController$$ExternalSyntheticLambda280 implements RequestDelegate {
    public static final /* synthetic */ MessagesController$$ExternalSyntheticLambda280 INSTANCE = new MessagesController$$ExternalSyntheticLambda280();

    private /* synthetic */ MessagesController$$ExternalSyntheticLambda280() {
    }

    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
        MessagesController.lambda$unblockPeer$85(tLObject, tL_error);
    }
}
