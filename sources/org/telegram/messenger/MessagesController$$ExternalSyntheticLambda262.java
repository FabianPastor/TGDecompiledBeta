package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class MessagesController$$ExternalSyntheticLambda262 implements RequestDelegate {
    public static final /* synthetic */ MessagesController$$ExternalSyntheticLambda262 INSTANCE = new MessagesController$$ExternalSyntheticLambda262();

    private /* synthetic */ MessagesController$$ExternalSyntheticLambda262() {
    }

    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
        MessagesController.lambda$unregistedPush$236(tLObject, tL_error);
    }
}
