package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

public final /* synthetic */ class MessagesController$$ExternalSyntheticLambda355 implements RequestDelegate {
    public static final /* synthetic */ MessagesController$$ExternalSyntheticLambda355 INSTANCE = new MessagesController$$ExternalSyntheticLambda355();

    private /* synthetic */ MessagesController$$ExternalSyntheticLambda355() {
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        MessagesController.lambda$reportSpam$55(tLObject, tLRPC$TL_error);
    }
}
