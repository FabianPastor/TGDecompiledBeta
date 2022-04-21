package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class MessagesController$$ExternalSyntheticLambda265 implements RequestDelegate {
    public static final /* synthetic */ MessagesController$$ExternalSyntheticLambda265 INSTANCE = new MessagesController$$ExternalSyntheticLambda265();

    private /* synthetic */ MessagesController$$ExternalSyntheticLambda265() {
    }

    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
        MessagesController.lambda$hidePromoDialog$105(tLObject, tL_error);
    }
}
