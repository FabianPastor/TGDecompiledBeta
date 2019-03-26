package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;

final /* synthetic */ class MessagesController$$Lambda$48 implements RequestDelegate {
    private final MessagesController arg$1;
    private final long arg$2;
    private final long arg$3;

    MessagesController$$Lambda$48(MessagesController messagesController, long j, long j2) {
        this.arg$1 = messagesController;
        this.arg$2 = j;
        this.arg$3 = j2;
    }

    public void run(TLObject tLObject, TL_error tL_error) {
        this.arg$1.lambda$deleteDialog$69$MessagesController(this.arg$2, this.arg$3, tLObject, tL_error);
    }
}
