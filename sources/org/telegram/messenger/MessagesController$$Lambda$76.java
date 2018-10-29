package org.telegram.messenger;

import org.telegram.tgnet.TLRPC.messages_Dialogs;

final /* synthetic */ class MessagesController$$Lambda$76 implements Runnable {
    private final MessagesController arg$1;
    private final messages_Dialogs arg$2;

    MessagesController$$Lambda$76(MessagesController messagesController, messages_Dialogs messages_dialogs) {
        this.arg$1 = messagesController;
        this.arg$2 = messages_dialogs;
    }

    public void run() {
        this.arg$1.lambda$processDialogsUpdate$117$MessagesController(this.arg$2);
    }
}
