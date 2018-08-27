package org.telegram.messenger;

import java.util.ArrayList;

final /* synthetic */ class MessagesController$$Lambda$159 implements Runnable {
    private final MessagesController arg$1;
    private final int arg$2;
    private final ArrayList arg$3;

    MessagesController$$Lambda$159(MessagesController messagesController, int i, ArrayList arrayList) {
        this.arg$1 = messagesController;
        this.arg$2 = i;
        this.arg$3 = arrayList;
    }

    public void run() {
        this.arg$1.lambda$null$210$MessagesController(this.arg$2, this.arg$3);
    }
}
