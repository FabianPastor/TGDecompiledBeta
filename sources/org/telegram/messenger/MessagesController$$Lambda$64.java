package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;

final /* synthetic */ class MessagesController$$Lambda$64 implements RequestDelegate {
    private final MessagesController arg$1;
    private final String arg$2;
    private final String arg$3;

    MessagesController$$Lambda$64(MessagesController messagesController, String str, String str2) {
        this.arg$1 = messagesController;
        this.arg$2 = str;
        this.arg$3 = str2;
    }

    public void run(TLObject tLObject, TL_error tL_error) {
        this.arg$1.lambda$checkProxyInfoInternal$95$MessagesController(this.arg$2, this.arg$3, tLObject, tL_error);
    }
}
