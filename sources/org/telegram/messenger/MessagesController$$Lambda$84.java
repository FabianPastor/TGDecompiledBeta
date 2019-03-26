package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_dialog;
import org.telegram.tgnet.TLRPC.TL_error;

final /* synthetic */ class MessagesController$$Lambda$84 implements RequestDelegate {
    private final MessagesController arg$1;
    private final int arg$2;
    private final TL_dialog arg$3;
    private final long arg$4;

    MessagesController$$Lambda$84(MessagesController messagesController, int i, TL_dialog tL_dialog, long j) {
        this.arg$1 = messagesController;
        this.arg$2 = i;
        this.arg$3 = tL_dialog;
        this.arg$4 = j;
    }

    public void run(TLObject tLObject, TL_error tL_error) {
        this.arg$1.lambda$checkLastDialogMessage$133$MessagesController(this.arg$2, this.arg$3, this.arg$4, tLObject, tL_error);
    }
}
