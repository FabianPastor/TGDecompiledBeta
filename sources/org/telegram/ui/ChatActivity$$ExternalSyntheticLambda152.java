package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

public final /* synthetic */ class ChatActivity$$ExternalSyntheticLambda152 implements RequestDelegate {
    public static final /* synthetic */ ChatActivity$$ExternalSyntheticLambda152 INSTANCE = new ChatActivity$$ExternalSyntheticLambda152();

    private /* synthetic */ ChatActivity$$ExternalSyntheticLambda152() {
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        ChatActivity.lambda$markSponsoredAsRead$162(tLObject, tLRPC$TL_error);
    }
}
