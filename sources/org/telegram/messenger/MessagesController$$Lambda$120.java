package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;

final /* synthetic */ class MessagesController$$Lambda$120 implements RequestDelegate {
    private final MessagesController arg$1;
    private final int arg$2;
    private final int arg$3;

    MessagesController$$Lambda$120(MessagesController messagesController, int i, int i2) {
        this.arg$1 = messagesController;
        this.arg$2 = i;
        this.arg$3 = i2;
    }

    public void run(TLObject tLObject, TL_error tL_error) {
        this.arg$1.lambda$getDifference$196$MessagesController(this.arg$2, this.arg$3, tLObject, tL_error);
    }
}
