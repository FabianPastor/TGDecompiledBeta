package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

public final /* synthetic */ class MessagesController$$ExternalSyntheticLambda343 implements RequestDelegate {
    public static final /* synthetic */ MessagesController$$ExternalSyntheticLambda343 INSTANCE = new MessagesController$$ExternalSyntheticLambda343();

    private /* synthetic */ MessagesController$$ExternalSyntheticLambda343() {
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        MessagesController.lambda$removeSuggestion$22(tLObject, tLRPC$TL_error);
    }
}
