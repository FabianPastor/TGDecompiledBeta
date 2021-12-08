package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class MessagesController$$ExternalSyntheticLambda257 implements RequestDelegate {
    public static final /* synthetic */ MessagesController$$ExternalSyntheticLambda257 INSTANCE = new MessagesController$$ExternalSyntheticLambda257();

    private /* synthetic */ MessagesController$$ExternalSyntheticLambda257() {
    }

    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
        MessagesController.lambda$reportSpam$48(tLObject, tL_error);
    }
}
