package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

public final /* synthetic */ class MessagesController$$ExternalSyntheticLambda335 implements RequestDelegate {
    public static final /* synthetic */ MessagesController$$ExternalSyntheticLambda335 INSTANCE = new MessagesController$$ExternalSyntheticLambda335();

    private /* synthetic */ MessagesController$$ExternalSyntheticLambda335() {
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        MessagesController.lambda$unblockPeer$78(tLObject, tLRPC$TL_error);
    }
}
