package org.telegram.messenger;

import java.util.ArrayList;

final /* synthetic */ class MessagesController$$Lambda$236 implements Runnable {
    private final MessagesController arg$1;
    private final long arg$2;
    private final ArrayList arg$3;
    private final ArrayList arg$4;

    MessagesController$$Lambda$236(MessagesController messagesController, long j, ArrayList arrayList, ArrayList arrayList2) {
        this.arg$1 = messagesController;
        this.arg$2 = j;
        this.arg$3 = arrayList;
        this.arg$4 = arrayList2;
    }

    public void run() {
        this.arg$1.lambda$null$19$MessagesController(this.arg$2, this.arg$3, this.arg$4);
    }
}
