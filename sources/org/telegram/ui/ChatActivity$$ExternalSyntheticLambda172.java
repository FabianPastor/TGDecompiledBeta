package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

public final /* synthetic */ class ChatActivity$$ExternalSyntheticLambda172 implements RequestDelegate {
    public static final /* synthetic */ ChatActivity$$ExternalSyntheticLambda172 INSTANCE = new ChatActivity$$ExternalSyntheticLambda172();

    private /* synthetic */ ChatActivity$$ExternalSyntheticLambda172() {
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        ChatActivity.lambda$markSponsoredAsRead$185(tLObject, tLRPC$TL_error);
    }
}
