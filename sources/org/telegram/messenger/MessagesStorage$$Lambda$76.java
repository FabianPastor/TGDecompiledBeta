package org.telegram.messenger;

import android.util.SparseArray;

final /* synthetic */ class MessagesStorage$$Lambda$76 implements Runnable {
    private final MessagesStorage arg$1;
    private final SparseArray arg$2;
    private final boolean arg$3;

    MessagesStorage$$Lambda$76(MessagesStorage messagesStorage, SparseArray sparseArray, boolean z) {
        this.arg$1 = messagesStorage;
        this.arg$2 = sparseArray;
        this.arg$3 = z;
    }

    public void run() {
        this.arg$1.lambda$putChannelViews$103$MessagesStorage(this.arg$2, this.arg$3);
    }
}
