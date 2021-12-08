package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class MessagesController$$ExternalSyntheticLambda243 implements RequestDelegate {
    public static final /* synthetic */ MessagesController$$ExternalSyntheticLambda243 INSTANCE = new MessagesController$$ExternalSyntheticLambda243();

    private /* synthetic */ MessagesController$$ExternalSyntheticLambda243() {
    }

    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
        MessagesController.lambda$completeReadTask$188(tLObject, tL_error);
    }
}
