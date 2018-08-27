package org.telegram.messenger;

import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;

final /* synthetic */ class MessagesController$$Lambda$225 implements Runnable {
    private final MessagesController arg$1;
    private final TL_error arg$2;
    private final TLObject arg$3;
    private final Integer arg$4;

    MessagesController$$Lambda$225(MessagesController messagesController, TL_error tL_error, TLObject tLObject, Integer num) {
        this.arg$1 = messagesController;
        this.arg$2 = tL_error;
        this.arg$3 = tLObject;
        this.arg$4 = num;
    }

    public void run() {
        this.arg$1.lambda$null$62$MessagesController(this.arg$2, this.arg$3, this.arg$4);
    }
}
