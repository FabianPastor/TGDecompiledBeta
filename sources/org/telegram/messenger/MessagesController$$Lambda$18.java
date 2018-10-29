package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;

final /* synthetic */ class MessagesController$$Lambda$18 implements RequestDelegate {
    static final RequestDelegate $instance = new MessagesController$$Lambda$18();

    private MessagesController$$Lambda$18() {
    }

    public void run(TLObject tLObject, TL_error tL_error) {
        MessagesController.lambda$reportSpam$23$MessagesController(tLObject, tL_error);
    }
}
