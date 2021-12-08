package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class MessagesController$$ExternalSyntheticLambda253 implements RequestDelegate {
    public static final /* synthetic */ MessagesController$$ExternalSyntheticLambda253 INSTANCE = new MessagesController$$ExternalSyntheticLambda253();

    private /* synthetic */ MessagesController$$ExternalSyntheticLambda253() {
    }

    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
        MessagesController.lambda$markMessageContentAsRead$180(tLObject, tL_error);
    }
}
