package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

public final /* synthetic */ class MessagesController$$ExternalSyntheticLambda321 implements RequestDelegate {
    public static final /* synthetic */ MessagesController$$ExternalSyntheticLambda321 INSTANCE = new MessagesController$$ExternalSyntheticLambda321();

    private /* synthetic */ MessagesController$$ExternalSyntheticLambda321() {
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        MessagesController.lambda$hidePromoDialog$98(tLObject, tLRPC$TL_error);
    }
}
