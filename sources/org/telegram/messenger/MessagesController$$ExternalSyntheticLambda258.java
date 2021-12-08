package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class MessagesController$$ExternalSyntheticLambda258 implements RequestDelegate {
    public static final /* synthetic */ MessagesController$$ExternalSyntheticLambda258 INSTANCE = new MessagesController$$ExternalSyntheticLambda258();

    private /* synthetic */ MessagesController$$ExternalSyntheticLambda258() {
    }

    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
        MessagesController.lambda$reportSpam$49(tLObject, tL_error);
    }
}
