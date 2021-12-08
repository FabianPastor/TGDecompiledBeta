package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class MessagesController$$ExternalSyntheticLambda241 implements RequestDelegate {
    public static final /* synthetic */ MessagesController$$ExternalSyntheticLambda241 INSTANCE = new MessagesController$$ExternalSyntheticLambda241();

    private /* synthetic */ MessagesController$$ExternalSyntheticLambda241() {
    }

    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
        MessagesController.lambda$blockPeer$62(tLObject, tL_error);
    }
}
