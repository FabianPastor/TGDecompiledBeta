package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

public final /* synthetic */ class MessagesController$$ExternalSyntheticLambda339 implements RequestDelegate {
    public static final /* synthetic */ MessagesController$$ExternalSyntheticLambda339 INSTANCE = new MessagesController$$ExternalSyntheticLambda339();

    private /* synthetic */ MessagesController$$ExternalSyntheticLambda339() {
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        MessagesController.lambda$completeReadTask$188(tLObject, tLRPC$TL_error);
    }
}