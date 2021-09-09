package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

public final /* synthetic */ class ChatActivity$$ExternalSyntheticLambda155 implements RequestDelegate {
    public static final /* synthetic */ ChatActivity$$ExternalSyntheticLambda155 INSTANCE = new ChatActivity$$ExternalSyntheticLambda155();

    private /* synthetic */ ChatActivity$$ExternalSyntheticLambda155() {
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        ChatActivity.lambda$markSponsoredAsRead$164(tLObject, tLRPC$TL_error);
    }
}
