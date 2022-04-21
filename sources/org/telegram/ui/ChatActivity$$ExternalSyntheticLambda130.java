package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class ChatActivity$$ExternalSyntheticLambda130 implements RequestDelegate {
    public static final /* synthetic */ ChatActivity$$ExternalSyntheticLambda130 INSTANCE = new ChatActivity$$ExternalSyntheticLambda130();

    private /* synthetic */ ChatActivity$$ExternalSyntheticLambda130() {
    }

    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
        ChatActivity.lambda$markSponsoredAsRead$232(tLObject, tL_error);
    }
}
