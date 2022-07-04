package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class MessagesController$$ExternalSyntheticLambda290 implements RequestDelegate {
    public static final /* synthetic */ MessagesController$$ExternalSyntheticLambda290 INSTANCE = new MessagesController$$ExternalSyntheticLambda290();

    private /* synthetic */ MessagesController$$ExternalSyntheticLambda290() {
    }

    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
        MessagesController.lambda$unregistedPush$252(tLObject, tL_error);
    }
}
