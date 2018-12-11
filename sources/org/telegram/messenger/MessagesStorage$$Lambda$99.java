package org.telegram.messenger;

import org.telegram.tgnet.TLRPC.messages_Dialogs;

final /* synthetic */ class MessagesStorage$$Lambda$99 implements Runnable {
    private final MessagesStorage arg$1;
    private final messages_Dialogs arg$2;
    private final int arg$3;

    MessagesStorage$$Lambda$99(MessagesStorage messagesStorage, messages_Dialogs messages_dialogs, int i) {
        this.arg$1 = messagesStorage;
        this.arg$2 = messages_dialogs;
        this.arg$3 = i;
    }

    public void run() {
        this.arg$1.lambda$putDialogs$128$MessagesStorage(this.arg$2, this.arg$3);
    }
}
