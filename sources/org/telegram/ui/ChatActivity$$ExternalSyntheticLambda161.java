package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

public final /* synthetic */ class ChatActivity$$ExternalSyntheticLambda161 implements RequestDelegate {
    public static final /* synthetic */ ChatActivity$$ExternalSyntheticLambda161 INSTANCE = new ChatActivity$$ExternalSyntheticLambda161();

    private /* synthetic */ ChatActivity$$ExternalSyntheticLambda161() {
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        ChatActivity.lambda$markSponsoredAsRead$170(tLObject, tLRPC$TL_error);
    }
}
