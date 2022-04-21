package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class MessagesController$$ExternalSyntheticLambda271 implements RequestDelegate {
    public static final /* synthetic */ MessagesController$$ExternalSyntheticLambda271 INSTANCE = new MessagesController$$ExternalSyntheticLambda271();

    private /* synthetic */ MessagesController$$ExternalSyntheticLambda271() {
    }

    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
        MessagesController.lambda$markMessageContentAsRead$188(tLObject, tL_error);
    }
}
