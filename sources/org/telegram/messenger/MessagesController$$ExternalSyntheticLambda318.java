package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

public final /* synthetic */ class MessagesController$$ExternalSyntheticLambda318 implements RequestDelegate {
    public static final /* synthetic */ MessagesController$$ExternalSyntheticLambda318 INSTANCE = new MessagesController$$ExternalSyntheticLambda318();

    private /* synthetic */ MessagesController$$ExternalSyntheticLambda318() {
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        MessagesController.lambda$unblockPeer$78(tLObject, tLRPC$TL_error);
    }
}
