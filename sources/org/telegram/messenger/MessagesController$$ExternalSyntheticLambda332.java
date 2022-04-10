package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

public final /* synthetic */ class MessagesController$$ExternalSyntheticLambda332 implements RequestDelegate {
    public static final /* synthetic */ MessagesController$$ExternalSyntheticLambda332 INSTANCE = new MessagesController$$ExternalSyntheticLambda332();

    private /* synthetic */ MessagesController$$ExternalSyntheticLambda332() {
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        MessagesController.lambda$processUpdates$297(tLObject, tLRPC$TL_error);
    }
}
