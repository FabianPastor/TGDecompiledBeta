package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class MessagesController$$ExternalSyntheticLambda251 implements RequestDelegate {
    public static final /* synthetic */ MessagesController$$ExternalSyntheticLambda251 INSTANCE = new MessagesController$$ExternalSyntheticLambda251();

    private /* synthetic */ MessagesController$$ExternalSyntheticLambda251() {
    }

    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
        MessagesController.lambda$markMentionMessageAsRead$182(tLObject, tL_error);
    }
}
