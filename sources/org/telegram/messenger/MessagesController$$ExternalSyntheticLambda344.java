package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

public final /* synthetic */ class MessagesController$$ExternalSyntheticLambda344 implements RequestDelegate {
    public static final /* synthetic */ MessagesController$$ExternalSyntheticLambda344 INSTANCE = new MessagesController$$ExternalSyntheticLambda344();

    private /* synthetic */ MessagesController$$ExternalSyntheticLambda344() {
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        MessagesController.lambda$removeSuggestion$22(tLObject, tLRPC$TL_error);
    }
}
