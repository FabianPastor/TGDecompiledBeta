package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.tgnet.TLRPC.TL_messages_getMessagesViews;

final /* synthetic */ class MessagesController$$Lambda$51 implements RequestDelegate {
    private final MessagesController arg$1;
    private final int arg$2;
    private final TL_messages_getMessagesViews arg$3;

    MessagesController$$Lambda$51(MessagesController messagesController, int i, TL_messages_getMessagesViews tL_messages_getMessagesViews) {
        this.arg$1 = messagesController;
        this.arg$2 = i;
        this.arg$3 = tL_messages_getMessagesViews;
    }

    public void run(TLObject tLObject, TL_error tL_error) {
        this.arg$1.lambda$updateTimerProc$68$MessagesController(this.arg$2, this.arg$3, tLObject, tL_error);
    }
}
