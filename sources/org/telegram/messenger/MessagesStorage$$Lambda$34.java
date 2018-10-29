package org.telegram.messenger;

import java.util.ArrayList;

final /* synthetic */ class MessagesStorage$$Lambda$34 implements Runnable {
    private final MessagesStorage arg$1;
    private final ArrayList arg$2;
    private final int arg$3;
    private final int arg$4;
    private final int arg$5;
    private final int arg$6;

    MessagesStorage$$Lambda$34(MessagesStorage messagesStorage, ArrayList arrayList, int i, int i2, int i3, int i4) {
        this.arg$1 = messagesStorage;
        this.arg$2 = arrayList;
        this.arg$3 = i;
        this.arg$4 = i2;
        this.arg$5 = i3;
        this.arg$6 = i4;
    }

    public void run() {
        this.arg$1.lambda$createTaskForSecretChat$53$MessagesStorage(this.arg$2, this.arg$3, this.arg$4, this.arg$5, this.arg$6);
    }
}
