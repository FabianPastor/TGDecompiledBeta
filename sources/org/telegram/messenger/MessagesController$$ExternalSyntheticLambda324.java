package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

public final /* synthetic */ class MessagesController$$ExternalSyntheticLambda324 implements RequestDelegate {
    public static final /* synthetic */ MessagesController$$ExternalSyntheticLambda324 INSTANCE = new MessagesController$$ExternalSyntheticLambda324();

    private /* synthetic */ MessagesController$$ExternalSyntheticLambda324() {
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        MessagesController.lambda$completeReadTask$188(tLObject, tLRPC$TL_error);
    }
}
