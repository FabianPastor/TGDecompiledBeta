package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

public final /* synthetic */ class MessagesController$$ExternalSyntheticLambda359 implements RequestDelegate {
    public static final /* synthetic */ MessagesController$$ExternalSyntheticLambda359 INSTANCE = new MessagesController$$ExternalSyntheticLambda359();

    private /* synthetic */ MessagesController$$ExternalSyntheticLambda359() {
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        MessagesController.lambda$saveTheme$95(tLObject, tLRPC$TL_error);
    }
}
