package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

public final /* synthetic */ class MessagesController$$ExternalSyntheticLambda310 implements RequestDelegate {
    public static final /* synthetic */ MessagesController$$ExternalSyntheticLambda310 INSTANCE = new MessagesController$$ExternalSyntheticLambda310();

    private /* synthetic */ MessagesController$$ExternalSyntheticLambda310() {
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        MessagesController.lambda$reportSpam$49(tLObject, tLRPC$TL_error);
    }
}
