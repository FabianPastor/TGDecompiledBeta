package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

public final /* synthetic */ class MessagesController$$ExternalSyntheticLambda307 implements RequestDelegate {
    public static final /* synthetic */ MessagesController$$ExternalSyntheticLambda307 INSTANCE = new MessagesController$$ExternalSyntheticLambda307();

    private /* synthetic */ MessagesController$$ExternalSyntheticLambda307() {
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        MessagesController.lambda$blockPeer$62(tLObject, tLRPC$TL_error);
    }
}
