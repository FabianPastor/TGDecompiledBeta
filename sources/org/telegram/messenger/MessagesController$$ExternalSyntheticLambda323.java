package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

public final /* synthetic */ class MessagesController$$ExternalSyntheticLambda323 implements RequestDelegate {
    public static final /* synthetic */ MessagesController$$ExternalSyntheticLambda323 INSTANCE = new MessagesController$$ExternalSyntheticLambda323();

    private /* synthetic */ MessagesController$$ExternalSyntheticLambda323() {
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        MessagesController.lambda$removeSuggestion$16(tLObject, tLRPC$TL_error);
    }
}