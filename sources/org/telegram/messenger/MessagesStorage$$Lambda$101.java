package org.telegram.messenger;

import java.util.ArrayList;

final /* synthetic */ class MessagesStorage$$Lambda$101 implements Runnable {
    private final MessagesStorage arg$1;
    private final ArrayList arg$2;

    MessagesStorage$$Lambda$101(MessagesStorage messagesStorage, ArrayList arrayList) {
        this.arg$1 = messagesStorage;
        this.arg$2 = arrayList;
    }

    public void run() {
        this.arg$1.lambda$null$99$MessagesStorage(this.arg$2);
    }
}
