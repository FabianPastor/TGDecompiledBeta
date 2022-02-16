package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

public final /* synthetic */ class ChatActivity$$ExternalSyntheticLambda203 implements RequestDelegate {
    public static final /* synthetic */ ChatActivity$$ExternalSyntheticLambda203 INSTANCE = new ChatActivity$$ExternalSyntheticLambda203();

    private /* synthetic */ ChatActivity$$ExternalSyntheticLambda203() {
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        ChatActivity.lambda$markSponsoredAsRead$218(tLObject, tLRPC$TL_error);
    }
}
