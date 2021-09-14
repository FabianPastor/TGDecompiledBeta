package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

public final /* synthetic */ class MessagesController$$ExternalSyntheticLambda319 implements RequestDelegate {
    public static final /* synthetic */ MessagesController$$ExternalSyntheticLambda319 INSTANCE = new MessagesController$$ExternalSyntheticLambda319();

    private /* synthetic */ MessagesController$$ExternalSyntheticLambda319() {
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        MessagesController.lambda$hidePeerSettingsBar$47(tLObject, tLRPC$TL_error);
    }
}
