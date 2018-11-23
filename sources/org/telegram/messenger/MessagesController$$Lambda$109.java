package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;

final /* synthetic */ class MessagesController$$Lambda$109 implements RequestDelegate {
    static final RequestDelegate $instance = new MessagesController$$Lambda$109();

    private MessagesController$$Lambda$109() {
    }

    public void run(TLObject tLObject, TL_error tL_error) {
        MessagesController.lambda$unregistedPush$168$MessagesController(tLObject, tL_error);
    }
}
