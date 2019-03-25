package org.telegram.messenger;

import android.util.LongSparseArray;

final /* synthetic */ class MessagesStorage$$Lambda$82 implements Runnable {
    private final MessagesStorage arg$1;
    private final LongSparseArray arg$2;

    MessagesStorage$$Lambda$82(MessagesStorage messagesStorage, LongSparseArray longSparseArray) {
        this.arg$1 = messagesStorage;
        this.arg$2 = longSparseArray;
    }

    public void run() {
        this.arg$1.lambda$putWebPages$112$MessagesStorage(this.arg$2);
    }
}
