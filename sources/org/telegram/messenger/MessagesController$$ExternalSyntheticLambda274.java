package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class MessagesController$$ExternalSyntheticLambda274 implements RequestDelegate {
    public static final /* synthetic */ MessagesController$$ExternalSyntheticLambda274 INSTANCE = new MessagesController$$ExternalSyntheticLambda274();

    private /* synthetic */ MessagesController$$ExternalSyntheticLambda274() {
    }

    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
        MessagesController.lambda$removeSuggestion$21(tLObject, tL_error);
    }
}
