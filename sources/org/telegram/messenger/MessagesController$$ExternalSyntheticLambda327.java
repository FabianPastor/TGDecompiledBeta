package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

public final /* synthetic */ class MessagesController$$ExternalSyntheticLambda327 implements RequestDelegate {
    public static final /* synthetic */ MessagesController$$ExternalSyntheticLambda327 INSTANCE = new MessagesController$$ExternalSyntheticLambda327();

    private /* synthetic */ MessagesController$$ExternalSyntheticLambda327() {
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        MessagesController.lambda$processUpdates$290(tLObject, tLRPC$TL_error);
    }
}
