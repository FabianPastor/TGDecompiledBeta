package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

public final /* synthetic */ class MessagesController$$ExternalSyntheticLambda350 implements RequestDelegate {
    public static final /* synthetic */ MessagesController$$ExternalSyntheticLambda350 INSTANCE = new MessagesController$$ExternalSyntheticLambda350();

    private /* synthetic */ MessagesController$$ExternalSyntheticLambda350() {
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        MessagesController.lambda$markReactionsAsRead$326(tLObject, tLRPC$TL_error);
    }
}
