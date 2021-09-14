package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

public final /* synthetic */ class MessagesController$$ExternalSyntheticLambda311 implements RequestDelegate {
    public static final /* synthetic */ MessagesController$$ExternalSyntheticLambda311 INSTANCE = new MessagesController$$ExternalSyntheticLambda311();

    private /* synthetic */ MessagesController$$ExternalSyntheticLambda311() {
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        MessagesController.lambda$reportSpam$48(tLObject, tLRPC$TL_error);
    }
}
