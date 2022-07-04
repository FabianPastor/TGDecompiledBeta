package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class MessagesController$$ExternalSyntheticLambda268 implements RequestDelegate {
    public static final /* synthetic */ MessagesController$$ExternalSyntheticLambda268 INSTANCE = new MessagesController$$ExternalSyntheticLambda268();

    private /* synthetic */ MessagesController$$ExternalSyntheticLambda268() {
    }

    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
        MessagesController.lambda$blockPeer$69(tLObject, tL_error);
    }
}
