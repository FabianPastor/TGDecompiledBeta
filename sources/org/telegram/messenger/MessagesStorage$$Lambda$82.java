package org.telegram.messenger;

import java.util.ArrayList;

final /* synthetic */ class MessagesStorage$$Lambda$82 implements Runnable {
    private final MessagesStorage arg$1;
    private final ArrayList arg$2;
    private final boolean arg$3;
    private final boolean arg$4;

    MessagesStorage$$Lambda$82(MessagesStorage messagesStorage, ArrayList arrayList, boolean z, boolean z2) {
        this.arg$1 = messagesStorage;
        this.arg$2 = arrayList;
        this.arg$3 = z;
        this.arg$4 = z2;
    }

    public void run() {
        this.arg$1.lambda$updateUsers$109$MessagesStorage(this.arg$2, this.arg$3, this.arg$4);
    }
}
