package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class MessagesController$$ExternalSyntheticLambda242 implements RequestDelegate {
    public static final /* synthetic */ MessagesController$$ExternalSyntheticLambda242 INSTANCE = new MessagesController$$ExternalSyntheticLambda242();

    private /* synthetic */ MessagesController$$ExternalSyntheticLambda242() {
    }

    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
        MessagesController.lambda$completeReadTask$186(tLObject, tL_error);
    }
}
