package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;

final /* synthetic */ class MessagesController$$Lambda$112 implements RequestDelegate {
    private final MessagesController arg$1;
    private final String arg$2;

    MessagesController$$Lambda$112(MessagesController messagesController, String str) {
        this.arg$1 = messagesController;
        this.arg$2 = str;
    }

    public void run(TLObject tLObject, TL_error tL_error) {
        this.arg$1.lambda$registerForPush$172$MessagesController(this.arg$2, tLObject, tL_error);
    }
}
