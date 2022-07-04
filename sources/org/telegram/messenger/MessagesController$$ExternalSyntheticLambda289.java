package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class MessagesController$$ExternalSyntheticLambda289 implements RequestDelegate {
    public static final /* synthetic */ MessagesController$$ExternalSyntheticLambda289 INSTANCE = new MessagesController$$ExternalSyntheticLambda289();

    private /* synthetic */ MessagesController$$ExternalSyntheticLambda289() {
    }

    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
        MessagesController.lambda$unblockPeer$88(tLObject, tL_error);
    }
}
