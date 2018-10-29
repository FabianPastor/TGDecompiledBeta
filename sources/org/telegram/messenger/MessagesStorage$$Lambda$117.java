package org.telegram.messenger;

import java.util.ArrayList;

final /* synthetic */ class MessagesStorage$$Lambda$117 implements Runnable {
    private final MessagesStorage arg$1;
    private final int arg$2;
    private final ArrayList arg$3;

    MessagesStorage$$Lambda$117(MessagesStorage messagesStorage, int i, ArrayList arrayList) {
        this.arg$1 = messagesStorage;
        this.arg$2 = i;
        this.arg$3 = arrayList;
    }

    public void run() {
        this.arg$1.lambda$null$22$MessagesStorage(this.arg$2, this.arg$3);
    }
}
