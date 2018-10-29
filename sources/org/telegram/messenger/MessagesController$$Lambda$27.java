package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;

final /* synthetic */ class MessagesController$$Lambda$27 implements RequestDelegate {
    private final MessagesController arg$1;
    private final int arg$2;
    private final int arg$3;
    private final long arg$4;
    private final int arg$5;

    MessagesController$$Lambda$27(MessagesController messagesController, int i, int i2, long j, int i3) {
        this.arg$1 = messagesController;
        this.arg$2 = i;
        this.arg$3 = i2;
        this.arg$4 = j;
        this.arg$5 = i3;
    }

    public void run(TLObject tLObject, TL_error tL_error) {
        this.arg$1.lambda$loadDialogPhotos$36$MessagesController(this.arg$2, this.arg$3, this.arg$4, this.arg$5, tLObject, tL_error);
    }
}
