package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

public final /* synthetic */ class ChatActivity$$ExternalSyntheticLambda151 implements RequestDelegate {
    public static final /* synthetic */ ChatActivity$$ExternalSyntheticLambda151 INSTANCE = new ChatActivity$$ExternalSyntheticLambda151();

    private /* synthetic */ ChatActivity$$ExternalSyntheticLambda151() {
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        ChatActivity.lambda$markSponsoredAsRead$161(tLObject, tLRPC$TL_error);
    }
}
