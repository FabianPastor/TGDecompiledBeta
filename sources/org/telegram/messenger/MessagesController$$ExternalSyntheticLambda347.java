package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

public final /* synthetic */ class MessagesController$$ExternalSyntheticLambda347 implements RequestDelegate {
    public static final /* synthetic */ MessagesController$$ExternalSyntheticLambda347 INSTANCE = new MessagesController$$ExternalSyntheticLambda347();

    private /* synthetic */ MessagesController$$ExternalSyntheticLambda347() {
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        MessagesController.lambda$deleteUserPhoto$89(tLObject, tLRPC$TL_error);
    }
}