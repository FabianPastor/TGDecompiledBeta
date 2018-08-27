package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;

final /* synthetic */ class MessagesController$$Lambda$123 implements RequestDelegate {
    private final MessagesController arg$1;
    private final long arg$2;

    MessagesController$$Lambda$123(MessagesController messagesController, long j) {
        this.arg$1 = messagesController;
        this.arg$2 = j;
    }

    public void run(TLObject tLObject, TL_error tL_error) {
        this.arg$1.lambda$pinDialog$200$MessagesController(this.arg$2, tLObject, tL_error);
    }
}
