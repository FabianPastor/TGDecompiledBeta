package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

public final /* synthetic */ class MessagesController$$ExternalSyntheticLambda322 implements RequestDelegate {
    public static final /* synthetic */ MessagesController$$ExternalSyntheticLambda322 INSTANCE = new MessagesController$$ExternalSyntheticLambda322();

    private /* synthetic */ MessagesController$$ExternalSyntheticLambda322() {
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        MessagesController.lambda$deleteUserPhoto$83(tLObject, tLRPC$TL_error);
    }
}
