package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

public final /* synthetic */ class ChatActivity$$ExternalSyntheticLambda174 implements RequestDelegate {
    public static final /* synthetic */ ChatActivity$$ExternalSyntheticLambda174 INSTANCE = new ChatActivity$$ExternalSyntheticLambda174();

    private /* synthetic */ ChatActivity$$ExternalSyntheticLambda174() {
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        ChatActivity.lambda$markSponsoredAsRead$188(tLObject, tLRPC$TL_error);
    }
}
