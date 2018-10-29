package org.telegram.messenger;

import org.telegram.tgnet.TLObject;

final /* synthetic */ class MessagesStorage$$Lambda$40 implements Runnable {
    private final MessagesStorage arg$1;
    private final TLObject arg$2;
    private final String arg$3;

    MessagesStorage$$Lambda$40(MessagesStorage messagesStorage, TLObject tLObject, String str) {
        this.arg$1 = messagesStorage;
        this.arg$2 = tLObject;
        this.arg$3 = str;
    }

    public void run() {
        this.arg$1.lambda$saveBotCache$60$MessagesStorage(this.arg$2, this.arg$3);
    }
}
