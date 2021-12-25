package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

public final /* synthetic */ class ChatActivity$$ExternalSyntheticLambda168 implements RequestDelegate {
    public static final /* synthetic */ ChatActivity$$ExternalSyntheticLambda168 INSTANCE = new ChatActivity$$ExternalSyntheticLambda168();

    private /* synthetic */ ChatActivity$$ExternalSyntheticLambda168() {
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        ChatActivity.lambda$markSponsoredAsRead$181(tLObject, tLRPC$TL_error);
    }
}
