package org.telegram.messenger;

import java.util.ArrayList;

final /* synthetic */ class MessagesStorage$$Lambda$94 implements Runnable {
    private final MessagesStorage arg$1;
    private final ArrayList arg$2;
    private final int arg$3;

    MessagesStorage$$Lambda$94(MessagesStorage messagesStorage, ArrayList arrayList, int i) {
        this.arg$1 = messagesStorage;
        this.arg$2 = arrayList;
        this.arg$3 = i;
    }

    public void run() {
        this.arg$1.lambda$markMessagesAsDeleted$125$MessagesStorage(this.arg$2, this.arg$3);
    }
}
