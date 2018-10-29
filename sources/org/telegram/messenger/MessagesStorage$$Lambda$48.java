package org.telegram.messenger;

import java.util.ArrayList;

final /* synthetic */ class MessagesStorage$$Lambda$48 implements Runnable {
    private final MessagesStorage arg$1;
    private final boolean arg$2;
    private final ArrayList arg$3;

    MessagesStorage$$Lambda$48(MessagesStorage messagesStorage, boolean z, ArrayList arrayList) {
        this.arg$1 = messagesStorage;
        this.arg$2 = z;
        this.arg$3 = arrayList;
    }

    public void run() {
        this.arg$1.lambda$putContacts$70$MessagesStorage(this.arg$2, this.arg$3);
    }
}
