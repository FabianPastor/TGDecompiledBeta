package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class MessagesController$$ExternalSyntheticLambda286 implements RequestDelegate {
    public static final /* synthetic */ MessagesController$$ExternalSyntheticLambda286 INSTANCE = new MessagesController$$ExternalSyntheticLambda286();

    private /* synthetic */ MessagesController$$ExternalSyntheticLambda286() {
    }

    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
        MessagesController.lambda$reportSpam$57(tLObject, tL_error);
    }
}
