package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;

final /* synthetic */ class MessagesController$$Lambda$100 implements RequestDelegate {
    private final MessagesController arg$1;
    private final int arg$2;
    private final String arg$3;

    MessagesController$$Lambda$100(MessagesController messagesController, int i, String str) {
        this.arg$1 = messagesController;
        this.arg$2 = i;
        this.arg$3 = str;
    }

    public void run(TLObject tLObject, TL_error tL_error) {
        this.arg$1.lambda$updateChannelUserName$154$MessagesController(this.arg$2, this.arg$3, tLObject, tL_error);
    }
}
