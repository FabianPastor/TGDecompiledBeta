package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.tgnet.TLRPC.TL_messages_saveRecentSticker;

final /* synthetic */ class MessagesController$$Lambda$51 implements RequestDelegate {
    private final MessagesController arg$1;
    private final Object arg$2;
    private final TL_messages_saveRecentSticker arg$3;

    MessagesController$$Lambda$51(MessagesController messagesController, Object obj, TL_messages_saveRecentSticker tL_messages_saveRecentSticker) {
        this.arg$1 = messagesController;
        this.arg$2 = obj;
        this.arg$3 = tL_messages_saveRecentSticker;
    }

    public void run(TLObject tLObject, TL_error tL_error) {
        this.arg$1.lambda$saveRecentSticker$71$MessagesController(this.arg$2, this.arg$3, tLObject, tL_error);
    }
}
