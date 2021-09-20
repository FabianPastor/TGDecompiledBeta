package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

public final /* synthetic */ class MessagesController$$ExternalSyntheticLambda317 implements RequestDelegate {
    public static final /* synthetic */ MessagesController$$ExternalSyntheticLambda317 INSTANCE = new MessagesController$$ExternalSyntheticLambda317();

    private /* synthetic */ MessagesController$$ExternalSyntheticLambda317() {
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        MessagesController.lambda$markMentionMessageAsRead$182(tLObject, tLRPC$TL_error);
    }
}
