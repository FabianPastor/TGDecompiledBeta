package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

public final /* synthetic */ class MessagesController$$ExternalSyntheticLambda331 implements RequestDelegate {
    public static final /* synthetic */ MessagesController$$ExternalSyntheticLambda331 INSTANCE = new MessagesController$$ExternalSyntheticLambda331();

    private /* synthetic */ MessagesController$$ExternalSyntheticLambda331() {
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        MessagesController.lambda$markMentionMessageAsRead$184(tLObject, tLRPC$TL_error);
    }
}
