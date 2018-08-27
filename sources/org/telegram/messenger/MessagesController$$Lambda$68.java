package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;

final /* synthetic */ class MessagesController$$Lambda$68 implements RequestDelegate {
    private final MessagesController arg$1;
    private final int arg$2;
    private final int arg$3;
    private final int arg$4;
    private final int arg$5;

    MessagesController$$Lambda$68(MessagesController messagesController, int i, int i2, int i3, int i4) {
        this.arg$1 = messagesController;
        this.arg$2 = i;
        this.arg$3 = i2;
        this.arg$4 = i3;
        this.arg$5 = i4;
    }

    public void run(TLObject tLObject, TL_error tL_error) {
        this.arg$1.lambda$resetDialogs$98$MessagesController(this.arg$2, this.arg$3, this.arg$4, this.arg$5, tLObject, tL_error);
    }
}
