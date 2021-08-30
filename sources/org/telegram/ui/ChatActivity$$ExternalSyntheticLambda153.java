package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

public final /* synthetic */ class ChatActivity$$ExternalSyntheticLambda153 implements RequestDelegate {
    public static final /* synthetic */ ChatActivity$$ExternalSyntheticLambda153 INSTANCE = new ChatActivity$$ExternalSyntheticLambda153();

    private /* synthetic */ ChatActivity$$ExternalSyntheticLambda153() {
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        ChatActivity.lambda$markSponsoredAsRead$163(tLObject, tLRPC$TL_error);
    }
}
