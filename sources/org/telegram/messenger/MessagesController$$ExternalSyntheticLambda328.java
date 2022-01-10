package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

public final /* synthetic */ class MessagesController$$ExternalSyntheticLambda328 implements RequestDelegate {
    public static final /* synthetic */ MessagesController$$ExternalSyntheticLambda328 INSTANCE = new MessagesController$$ExternalSyntheticLambda328();

    private /* synthetic */ MessagesController$$ExternalSyntheticLambda328() {
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        MessagesController.lambda$markMentionMessageAsRead$183(tLObject, tLRPC$TL_error);
    }
}
