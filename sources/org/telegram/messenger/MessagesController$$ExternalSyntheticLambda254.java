package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class MessagesController$$ExternalSyntheticLambda254 implements RequestDelegate {
    public static final /* synthetic */ MessagesController$$ExternalSyntheticLambda254 INSTANCE = new MessagesController$$ExternalSyntheticLambda254();

    private /* synthetic */ MessagesController$$ExternalSyntheticLambda254() {
    }

    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
        MessagesController.lambda$processUpdates$289(tLObject, tL_error);
    }
}
