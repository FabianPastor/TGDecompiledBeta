package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

public final /* synthetic */ class MessagesController$$ExternalSyntheticLambda340 implements RequestDelegate {
    public static final /* synthetic */ MessagesController$$ExternalSyntheticLambda340 INSTANCE = new MessagesController$$ExternalSyntheticLambda340();

    private /* synthetic */ MessagesController$$ExternalSyntheticLambda340() {
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        MessagesController.lambda$removeSuggestion$21(tLObject, tLRPC$TL_error);
    }
}
