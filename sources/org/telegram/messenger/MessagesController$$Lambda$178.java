package org.telegram.messenger;

import android.util.LongSparseArray;

final /* synthetic */ class MessagesController$$Lambda$178 implements Runnable {
    private final MessagesController arg$1;
    private final LongSparseArray arg$2;

    MessagesController$$Lambda$178(MessagesController messagesController, LongSparseArray longSparseArray) {
        this.arg$1 = messagesController;
        this.arg$2 = longSparseArray;
    }

    public void run() {
        this.arg$1.lambda$null$181$MessagesController(this.arg$2);
    }
}
