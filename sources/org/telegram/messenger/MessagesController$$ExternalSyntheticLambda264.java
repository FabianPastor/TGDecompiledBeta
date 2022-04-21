package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class MessagesController$$ExternalSyntheticLambda264 implements RequestDelegate {
    public static final /* synthetic */ MessagesController$$ExternalSyntheticLambda264 INSTANCE = new MessagesController$$ExternalSyntheticLambda264();

    private /* synthetic */ MessagesController$$ExternalSyntheticLambda264() {
    }

    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
        MessagesController.lambda$hidePeerSettingsBar$53(tLObject, tL_error);
    }
}
