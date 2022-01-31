package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

public final /* synthetic */ class MessagesController$$ExternalSyntheticLambda334 implements RequestDelegate {
    public static final /* synthetic */ MessagesController$$ExternalSyntheticLambda334 INSTANCE = new MessagesController$$ExternalSyntheticLambda334();

    private /* synthetic */ MessagesController$$ExternalSyntheticLambda334() {
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        MessagesController.lambda$unblockPeer$83(tLObject, tLRPC$TL_error);
    }
}
