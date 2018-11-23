package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.tgnet.TLRPC.TL_messages_saveGif;

final /* synthetic */ class MessagesController$$Lambda$45 implements RequestDelegate {
    private final MessagesController arg$1;
    private final Object arg$2;
    private final TL_messages_saveGif arg$3;

    MessagesController$$Lambda$45(MessagesController messagesController, Object obj, TL_messages_saveGif tL_messages_saveGif) {
        this.arg$1 = messagesController;
        this.arg$2 = obj;
        this.arg$3 = tL_messages_saveGif;
    }

    public void run(TLObject tLObject, TL_error tL_error) {
        this.arg$1.lambda$saveGif$60$MessagesController(this.arg$2, this.arg$3, tLObject, tL_error);
    }
}
