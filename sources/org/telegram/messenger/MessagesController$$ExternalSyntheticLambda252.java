package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class MessagesController$$ExternalSyntheticLambda252 implements RequestDelegate {
    public static final /* synthetic */ MessagesController$$ExternalSyntheticLambda252 INSTANCE = new MessagesController$$ExternalSyntheticLambda252();

    private /* synthetic */ MessagesController$$ExternalSyntheticLambda252() {
    }

    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
        MessagesController.lambda$markMentionsAsRead$190(tLObject, tL_error);
    }
}
