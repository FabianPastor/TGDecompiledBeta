package org.telegram.messenger;

import java.util.ArrayList;

final /* synthetic */ class MessagesStorage$$Lambda$86 implements Runnable {
    private final MessagesStorage arg$1;
    private final ArrayList arg$2;
    private final ArrayList arg$3;
    private final int arg$4;

    MessagesStorage$$Lambda$86(MessagesStorage messagesStorage, ArrayList arrayList, ArrayList arrayList2, int i) {
        this.arg$1 = messagesStorage;
        this.arg$2 = arrayList;
        this.arg$3 = arrayList2;
        this.arg$4 = i;
    }

    public void run() {
        this.arg$1.lambda$updateDialogsWithDeletedMessages$114$MessagesStorage(this.arg$2, this.arg$3, this.arg$4);
    }
}
