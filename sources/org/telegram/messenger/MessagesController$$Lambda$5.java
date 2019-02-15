package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.tgnet.TLRPC.TL_wallPaperSettings;

final /* synthetic */ class MessagesController$$Lambda$5 implements RequestDelegate {
    private final MessagesController arg$1;
    private final TL_wallPaperSettings arg$2;

    MessagesController$$Lambda$5(MessagesController messagesController, TL_wallPaperSettings tL_wallPaperSettings) {
        this.arg$1 = messagesController;
        this.arg$2 = tL_wallPaperSettings;
    }

    public void run(TLObject tLObject, TL_error tL_error) {
        this.arg$1.lambda$didReceivedNotification$6$MessagesController(this.arg$2, tLObject, tL_error);
    }
}
