package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

public final /* synthetic */ class MessagesController$$ExternalSyntheticLambda345 implements RequestDelegate {
    public static final /* synthetic */ MessagesController$$ExternalSyntheticLambda345 INSTANCE = new MessagesController$$ExternalSyntheticLambda345();

    private /* synthetic */ MessagesController$$ExternalSyntheticLambda345() {
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        MessagesController.lambda$hidePromoDialog$108(tLObject, tLRPC$TL_error);
    }
}
