package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class MessagesController$$ExternalSyntheticLambda248 implements RequestDelegate {
    public static final /* synthetic */ MessagesController$$ExternalSyntheticLambda248 INSTANCE = new MessagesController$$ExternalSyntheticLambda248();

    private /* synthetic */ MessagesController$$ExternalSyntheticLambda248() {
    }

    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
        MessagesController.lambda$hidePromoDialog$98(tLObject, tL_error);
    }
}
