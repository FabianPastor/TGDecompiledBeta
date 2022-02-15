package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

public final /* synthetic */ class ChatActivity$$ExternalSyntheticLambda202 implements RequestDelegate {
    public static final /* synthetic */ ChatActivity$$ExternalSyntheticLambda202 INSTANCE = new ChatActivity$$ExternalSyntheticLambda202();

    private /* synthetic */ ChatActivity$$ExternalSyntheticLambda202() {
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        ChatActivity.lambda$markSponsoredAsRead$217(tLObject, tLRPC$TL_error);
    }
}
