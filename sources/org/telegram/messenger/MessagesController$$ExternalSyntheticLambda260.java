package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class MessagesController$$ExternalSyntheticLambda260 implements RequestDelegate {
    public static final /* synthetic */ MessagesController$$ExternalSyntheticLambda260 INSTANCE = new MessagesController$$ExternalSyntheticLambda260();

    private /* synthetic */ MessagesController$$ExternalSyntheticLambda260() {
    }

    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
        MessagesController.lambda$saveTheme$85(tLObject, tL_error);
    }
}
