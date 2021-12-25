package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

public final /* synthetic */ class MessagesController$$ExternalSyntheticLambda338 implements RequestDelegate {
    public static final /* synthetic */ MessagesController$$ExternalSyntheticLambda338 INSTANCE = new MessagesController$$ExternalSyntheticLambda338();

    private /* synthetic */ MessagesController$$ExternalSyntheticLambda338() {
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        MessagesController.lambda$markMentionsAsRead$192(tLObject, tLRPC$TL_error);
    }
}
