package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

public final /* synthetic */ class MessagesController$$ExternalSyntheticLambda320 implements RequestDelegate {
    public static final /* synthetic */ MessagesController$$ExternalSyntheticLambda320 INSTANCE = new MessagesController$$ExternalSyntheticLambda320();

    private /* synthetic */ MessagesController$$ExternalSyntheticLambda320() {
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        MessagesController.lambda$completeReadTask$189(tLObject, tLRPC$TL_error);
    }
}
