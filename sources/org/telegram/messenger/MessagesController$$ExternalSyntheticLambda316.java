package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

public final /* synthetic */ class MessagesController$$ExternalSyntheticLambda316 implements RequestDelegate {
    public static final /* synthetic */ MessagesController$$ExternalSyntheticLambda316 INSTANCE = new MessagesController$$ExternalSyntheticLambda316();

    private /* synthetic */ MessagesController$$ExternalSyntheticLambda316() {
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        MessagesController.lambda$completeReadTask$186(tLObject, tLRPC$TL_error);
    }
}
