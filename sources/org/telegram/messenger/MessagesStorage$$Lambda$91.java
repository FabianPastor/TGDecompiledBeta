package org.telegram.messenger;

import java.util.ArrayList;

final /* synthetic */ class MessagesStorage$$Lambda$91 implements Runnable {
    private final MessagesStorage arg$1;
    private final ArrayList arg$2;

    MessagesStorage$$Lambda$91(MessagesStorage messagesStorage, ArrayList arrayList) {
        this.arg$1 = messagesStorage;
        this.arg$2 = arrayList;
    }

    public void run() {
        this.arg$1.lambda$unpinAllDialogsExceptNew$119$MessagesStorage(this.arg$2);
    }
}
