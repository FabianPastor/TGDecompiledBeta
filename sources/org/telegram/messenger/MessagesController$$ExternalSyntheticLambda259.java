package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class MessagesController$$ExternalSyntheticLambda259 implements RequestDelegate {
    public static final /* synthetic */ MessagesController$$ExternalSyntheticLambda259 INSTANCE = new MessagesController$$ExternalSyntheticLambda259();

    private /* synthetic */ MessagesController$$ExternalSyntheticLambda259() {
    }

    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
        MessagesController.lambda$reportSpam$50(tLObject, tL_error);
    }
}
