package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

public final /* synthetic */ class MessagesController$$ExternalSyntheticLambda337 implements RequestDelegate {
    public static final /* synthetic */ MessagesController$$ExternalSyntheticLambda337 INSTANCE = new MessagesController$$ExternalSyntheticLambda337();

    private /* synthetic */ MessagesController$$ExternalSyntheticLambda337() {
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        MessagesController.lambda$markMentionMessageAsRead$189(tLObject, tLRPC$TL_error);
    }
}
