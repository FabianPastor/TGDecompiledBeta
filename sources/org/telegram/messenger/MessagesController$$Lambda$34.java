package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLRPC.TL_messages_editChatAdmin;

final /* synthetic */ class MessagesController$$Lambda$34 implements Runnable {
    private final MessagesController arg$1;
    private final TL_messages_editChatAdmin arg$2;
    private final RequestDelegate arg$3;

    MessagesController$$Lambda$34(MessagesController messagesController, TL_messages_editChatAdmin tL_messages_editChatAdmin, RequestDelegate requestDelegate) {
        this.arg$1 = messagesController;
        this.arg$2 = tL_messages_editChatAdmin;
        this.arg$3 = requestDelegate;
    }

    public void run() {
        this.arg$1.lambda$setUserAdminRole$52$MessagesController(this.arg$2, this.arg$3);
    }
}
