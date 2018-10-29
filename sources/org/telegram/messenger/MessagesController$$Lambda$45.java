package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;

final /* synthetic */ class MessagesController$$Lambda$45 implements RequestDelegate {
    static final RequestDelegate $instance = new MessagesController$$Lambda$45();

    private MessagesController$$Lambda$45() {
    }

    public void run(TLObject tLObject, TL_error tL_error) {
        MessagesController.lambda$saveGif$60$MessagesController(tLObject, tL_error);
    }
}
