package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

public final /* synthetic */ class MessagesController$$ExternalSyntheticLambda342 implements RequestDelegate {
    public static final /* synthetic */ MessagesController$$ExternalSyntheticLambda342 INSTANCE = new MessagesController$$ExternalSyntheticLambda342();

    private /* synthetic */ MessagesController$$ExternalSyntheticLambda342() {
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        MessagesController.lambda$reportSpam$54(tLObject, tLRPC$TL_error);
    }
}