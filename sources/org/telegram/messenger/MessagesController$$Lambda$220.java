package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.tgnet.TLRPC.TL_help_proxyDataPromo;

final /* synthetic */ class MessagesController$$Lambda$220 implements RequestDelegate {
    private final MessagesController arg$1;
    private final TL_help_proxyDataPromo arg$2;
    private final long arg$3;

    MessagesController$$Lambda$220(MessagesController messagesController, TL_help_proxyDataPromo tL_help_proxyDataPromo, long j) {
        this.arg$1 = messagesController;
        this.arg$2 = tL_help_proxyDataPromo;
        this.arg$3 = j;
    }

    public void run(TLObject tLObject, TL_error tL_error) {
        this.arg$1.lambda$null$76$MessagesController(this.arg$2, this.arg$3, tLObject, tL_error);
    }
}
