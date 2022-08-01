package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

public final /* synthetic */ class ChatActivity$$ExternalSyntheticLambda232 implements RequestDelegate {
    public static final /* synthetic */ ChatActivity$$ExternalSyntheticLambda232 INSTANCE = new ChatActivity$$ExternalSyntheticLambda232();

    private /* synthetic */ ChatActivity$$ExternalSyntheticLambda232() {
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        ChatActivity.lambda$markSponsoredAsRead$243(tLObject, tLRPC$TL_error);
    }
}
