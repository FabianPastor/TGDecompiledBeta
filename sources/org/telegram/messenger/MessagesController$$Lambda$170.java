package org.telegram.messenger;

import android.util.LongSparseArray;

final /* synthetic */ class MessagesController$$Lambda$170 implements Runnable {
    private final MessagesController arg$1;
    private final LongSparseArray arg$2;

    MessagesController$$Lambda$170(MessagesController messagesController, LongSparseArray longSparseArray) {
        this.arg$1 = messagesController;
        this.arg$2 = longSparseArray;
    }

    public void run() {
        this.arg$1.lambda$null$191$MessagesController(this.arg$2);
    }
}
