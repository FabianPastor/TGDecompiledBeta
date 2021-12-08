package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class MessagesController$$ExternalSyntheticLambda247 implements RequestDelegate {
    public static final /* synthetic */ MessagesController$$ExternalSyntheticLambda247 INSTANCE = new MessagesController$$ExternalSyntheticLambda247();

    private /* synthetic */ MessagesController$$ExternalSyntheticLambda247() {
    }

    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
        MessagesController.lambda$hidePeerSettingsBar$47(tLObject, tL_error);
    }
}
