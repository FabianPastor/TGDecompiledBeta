package org.telegram.messenger;

import java.util.ArrayList;

final /* synthetic */ class MessagesStorage$$Lambda$78 implements Runnable {
    private final MessagesStorage arg$1;
    private final ArrayList arg$2;
    private final boolean arg$3;
    private final boolean arg$4;
    private final int arg$5;
    private final boolean arg$6;

    MessagesStorage$$Lambda$78(MessagesStorage messagesStorage, ArrayList arrayList, boolean z, boolean z2, int i, boolean z3) {
        this.arg$1 = messagesStorage;
        this.arg$2 = arrayList;
        this.arg$3 = z;
        this.arg$4 = z2;
        this.arg$5 = i;
        this.arg$6 = z3;
    }

    public void run() {
        this.arg$1.lambda$putMessages$105$MessagesStorage(this.arg$2, this.arg$3, this.arg$4, this.arg$5, this.arg$6);
    }
}
