package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.Chat;
import org.telegram.tgnet.TLRPC.TL_error;

final /* synthetic */ class MessagesController$$Lambda$156 implements RequestDelegate {
    private final MessagesController arg$1;
    private final Chat arg$2;
    private final int arg$3;

    MessagesController$$Lambda$156(MessagesController messagesController, Chat chat, int i) {
        this.arg$1 = messagesController;
        this.arg$2 = chat;
        this.arg$3 = i;
    }

    public void run(TLObject tLObject, TL_error tL_error) {
        this.arg$1.lambda$null$211$MessagesController(this.arg$2, this.arg$3, tLObject, tL_error);
    }
}
