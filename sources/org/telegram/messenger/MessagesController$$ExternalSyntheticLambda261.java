package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class MessagesController$$ExternalSyntheticLambda261 implements RequestDelegate {
    public static final /* synthetic */ MessagesController$$ExternalSyntheticLambda261 INSTANCE = new MessagesController$$ExternalSyntheticLambda261();

    private /* synthetic */ MessagesController$$ExternalSyntheticLambda261() {
    }

    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
        MessagesController.lambda$completeReadTask$196(tLObject, tL_error);
    }
}
