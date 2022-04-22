package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

public final /* synthetic */ class MessagesController$$ExternalSyntheticLambda349 implements RequestDelegate {
    public static final /* synthetic */ MessagesController$$ExternalSyntheticLambda349 INSTANCE = new MessagesController$$ExternalSyntheticLambda349();

    private /* synthetic */ MessagesController$$ExternalSyntheticLambda349() {
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        MessagesController.lambda$blockPeer$68(tLObject, tLRPC$TL_error);
    }
}
