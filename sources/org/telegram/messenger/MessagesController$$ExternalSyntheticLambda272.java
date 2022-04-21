package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class MessagesController$$ExternalSyntheticLambda272 implements RequestDelegate {
    public static final /* synthetic */ MessagesController$$ExternalSyntheticLambda272 INSTANCE = new MessagesController$$ExternalSyntheticLambda272();

    private /* synthetic */ MessagesController$$ExternalSyntheticLambda272() {
    }

    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
        MessagesController.lambda$markReactionsAsRead$328(tLObject, tL_error);
    }
}
