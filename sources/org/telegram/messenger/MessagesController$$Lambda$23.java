package org.telegram.messenger;

import java.util.ArrayList;

final /* synthetic */ class MessagesController$$Lambda$23 implements Runnable {
    private final MessagesController arg$1;
    private final ArrayList arg$2;
    private final int arg$3;

    MessagesController$$Lambda$23(MessagesController messagesController, ArrayList arrayList, int i) {
        this.arg$1 = messagesController;
        this.arg$2 = arrayList;
        this.arg$3 = i;
    }

    public void run() {
        this.arg$1.lambda$getNewDeleteTask$30$MessagesController(this.arg$2, this.arg$3);
    }
}
