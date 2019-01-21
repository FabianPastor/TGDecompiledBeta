package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;

final /* synthetic */ class ChatActivity$$Lambda$76 implements RequestDelegate {
    static final RequestDelegate $instance = new ChatActivity$$Lambda$76();

    private ChatActivity$$Lambda$76() {
    }

    public void run(TLObject tLObject, TL_error tL_error) {
        ChatActivity.lambda$null$64$ChatActivity(tLObject, tL_error);
    }
}
