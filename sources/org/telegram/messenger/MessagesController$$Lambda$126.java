package org.telegram.messenger;

import java.util.ArrayList;

final /* synthetic */ class MessagesController$$Lambda$126 implements Runnable {
    private final MessagesController arg$1;
    private final int arg$2;
    private final ArrayList arg$3;

    MessagesController$$Lambda$126(MessagesController messagesController, int i, ArrayList arrayList) {
        this.arg$1 = messagesController;
        this.arg$2 = i;
        this.arg$3 = arrayList;
    }

    public void run() {
        this.arg$1.lambda$generateJoinMessage$206$MessagesController(this.arg$2, this.arg$3);
    }
}
