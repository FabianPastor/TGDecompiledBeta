package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class MessagesController$$ExternalSyntheticLambda282 implements RequestDelegate {
    public static final /* synthetic */ MessagesController$$ExternalSyntheticLambda282 INSTANCE = new MessagesController$$ExternalSyntheticLambda282();

    private /* synthetic */ MessagesController$$ExternalSyntheticLambda282() {
    }

    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
        MessagesController.lambda$processUpdates$306(tLObject, tL_error);
    }
}
