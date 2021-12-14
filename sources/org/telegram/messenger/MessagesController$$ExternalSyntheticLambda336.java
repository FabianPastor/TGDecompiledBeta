package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

public final /* synthetic */ class MessagesController$$ExternalSyntheticLambda336 implements RequestDelegate {
    public static final /* synthetic */ MessagesController$$ExternalSyntheticLambda336 INSTANCE = new MessagesController$$ExternalSyntheticLambda336();

    private /* synthetic */ MessagesController$$ExternalSyntheticLambda336() {
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        MessagesController.lambda$reportSpam$50(tLObject, tLRPC$TL_error);
    }
}
