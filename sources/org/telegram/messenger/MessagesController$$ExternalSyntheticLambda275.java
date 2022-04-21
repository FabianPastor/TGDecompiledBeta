package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class MessagesController$$ExternalSyntheticLambda275 implements RequestDelegate {
    public static final /* synthetic */ MessagesController$$ExternalSyntheticLambda275 INSTANCE = new MessagesController$$ExternalSyntheticLambda275();

    private /* synthetic */ MessagesController$$ExternalSyntheticLambda275() {
    }

    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
        MessagesController.lambda$reportSpam$54(tLObject, tL_error);
    }
}
