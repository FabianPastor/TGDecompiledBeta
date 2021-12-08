package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class MessagesController$$ExternalSyntheticLambda256 implements RequestDelegate {
    public static final /* synthetic */ MessagesController$$ExternalSyntheticLambda256 INSTANCE = new MessagesController$$ExternalSyntheticLambda256();

    private /* synthetic */ MessagesController$$ExternalSyntheticLambda256() {
    }

    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
        MessagesController.lambda$removeSuggestion$16(tLObject, tL_error);
    }
}
