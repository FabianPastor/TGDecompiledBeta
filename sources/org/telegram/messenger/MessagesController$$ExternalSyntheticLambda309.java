package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

public final /* synthetic */ class MessagesController$$ExternalSyntheticLambda309 implements RequestDelegate {
    public static final /* synthetic */ MessagesController$$ExternalSyntheticLambda309 INSTANCE = new MessagesController$$ExternalSyntheticLambda309();

    private /* synthetic */ MessagesController$$ExternalSyntheticLambda309() {
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        MessagesController.lambda$reportSpam$48(tLObject, tLRPC$TL_error);
    }
}
