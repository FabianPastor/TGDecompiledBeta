package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.Chat;
import org.telegram.tgnet.TLRPC.TL_error;

final /* synthetic */ class MessagesController$$Lambda$117 implements RequestDelegate {
    private final MessagesController arg$1;
    private final long arg$2;
    private final Chat arg$3;

    MessagesController$$Lambda$117(MessagesController messagesController, long j, Chat chat) {
        this.arg$1 = messagesController;
        this.arg$2 = j;
        this.arg$3 = chat;
    }

    public void run(TLObject tLObject, TL_error tL_error) {
        this.arg$1.lambda$loadUnknownChannel$177$MessagesController(this.arg$2, this.arg$3, tLObject, tL_error);
    }
}
