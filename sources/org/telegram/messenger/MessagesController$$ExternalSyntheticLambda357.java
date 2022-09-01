package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

public final /* synthetic */ class MessagesController$$ExternalSyntheticLambda357 implements RequestDelegate {
    public static final /* synthetic */ MessagesController$$ExternalSyntheticLambda357 INSTANCE = new MessagesController$$ExternalSyntheticLambda357();

    private /* synthetic */ MessagesController$$ExternalSyntheticLambda357() {
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        MessagesController.lambda$unblockPeer$88(tLObject, tLRPC$TL_error);
    }
}
