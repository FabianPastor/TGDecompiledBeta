package org.telegram.messenger;

import java.util.ArrayList;

final /* synthetic */ class MessagesStorage$$Lambda$90 implements Runnable {
    private final MessagesStorage arg$1;
    private final ArrayList arg$2;
    private final ArrayList arg$3;
    private final int arg$4;

    MessagesStorage$$Lambda$90(MessagesStorage messagesStorage, ArrayList arrayList, ArrayList arrayList2, int i) {
        this.arg$1 = messagesStorage;
        this.arg$2 = arrayList;
        this.arg$3 = arrayList2;
        this.arg$4 = i;
    }

    public void run() {
        this.arg$1.lambda$updateDialogsWithDeletedMessages$119$MessagesStorage(this.arg$2, this.arg$3, this.arg$4);
    }
}
