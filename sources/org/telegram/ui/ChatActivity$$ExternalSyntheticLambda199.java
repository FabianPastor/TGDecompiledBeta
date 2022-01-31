package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

public final /* synthetic */ class ChatActivity$$ExternalSyntheticLambda199 implements RequestDelegate {
    public static final /* synthetic */ ChatActivity$$ExternalSyntheticLambda199 INSTANCE = new ChatActivity$$ExternalSyntheticLambda199();

    private /* synthetic */ ChatActivity$$ExternalSyntheticLambda199() {
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        ChatActivity.lambda$markSponsoredAsRead$214(tLObject, tLRPC$TL_error);
    }
}
