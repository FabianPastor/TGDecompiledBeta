package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class MessagesController$$ExternalSyntheticLambda273 implements RequestDelegate {
    public static final /* synthetic */ MessagesController$$ExternalSyntheticLambda273 INSTANCE = new MessagesController$$ExternalSyntheticLambda273();

    private /* synthetic */ MessagesController$$ExternalSyntheticLambda273() {
    }

    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
        MessagesController.lambda$processUpdates$298(tLObject, tL_error);
    }
}
