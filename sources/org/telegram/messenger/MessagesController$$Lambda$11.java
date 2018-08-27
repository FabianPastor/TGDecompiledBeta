package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;

final /* synthetic */ class MessagesController$$Lambda$11 implements RequestDelegate {
    private final MessagesController arg$1;
    private final int arg$2;

    MessagesController$$Lambda$11(MessagesController messagesController, int i) {
        this.arg$1 = messagesController;
        this.arg$2 = i;
    }

    public void run(TLObject tLObject, TL_error tL_error) {
        this.arg$1.lambda$loadChannelAdmins$11$MessagesController(this.arg$2, tLObject, tL_error);
    }
}
