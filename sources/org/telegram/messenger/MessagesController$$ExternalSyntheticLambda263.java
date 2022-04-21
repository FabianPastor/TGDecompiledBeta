package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class MessagesController$$ExternalSyntheticLambda263 implements RequestDelegate {
    public static final /* synthetic */ MessagesController$$ExternalSyntheticLambda263 INSTANCE = new MessagesController$$ExternalSyntheticLambda263();

    private /* synthetic */ MessagesController$$ExternalSyntheticLambda263() {
    }

    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
        MessagesController.lambda$deleteUserPhoto$90(tLObject, tL_error);
    }
}
