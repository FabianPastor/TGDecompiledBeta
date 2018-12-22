package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;

final /* synthetic */ class MessagesController$$Lambda$135 implements RequestDelegate {
    static final RequestDelegate $instance = new MessagesController$$Lambda$135();

    private MessagesController$$Lambda$135() {
    }

    public void run(TLObject tLObject, TL_error tL_error) {
        MessagesController.lambda$processUpdates$224$MessagesController(tLObject, tL_error);
    }
}
