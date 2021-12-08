package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class ChatActivity$$ExternalSyntheticLambda68 implements RequestDelegate {
    public static final /* synthetic */ ChatActivity$$ExternalSyntheticLambda68 INSTANCE = new ChatActivity$$ExternalSyntheticLambda68();

    private /* synthetic */ ChatActivity$$ExternalSyntheticLambda68() {
    }

    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
        ChatActivity.lambda$markSponsoredAsRead$170(tLObject, tL_error);
    }
}
