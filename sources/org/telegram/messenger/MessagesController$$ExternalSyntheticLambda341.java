package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

public final /* synthetic */ class MessagesController$$ExternalSyntheticLambda341 implements RequestDelegate {
    public static final /* synthetic */ MessagesController$$ExternalSyntheticLambda341 INSTANCE = new MessagesController$$ExternalSyntheticLambda341();

    private /* synthetic */ MessagesController$$ExternalSyntheticLambda341() {
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        MessagesController.lambda$completeReadTask$195(tLObject, tLRPC$TL_error);
    }
}
