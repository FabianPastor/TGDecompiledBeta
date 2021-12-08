package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class MessagesController$$ExternalSyntheticLambda246 implements RequestDelegate {
    public static final /* synthetic */ MessagesController$$ExternalSyntheticLambda246 INSTANCE = new MessagesController$$ExternalSyntheticLambda246();

    private /* synthetic */ MessagesController$$ExternalSyntheticLambda246() {
    }

    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
        MessagesController.lambda$deleteUserPhoto$83(tLObject, tL_error);
    }
}
