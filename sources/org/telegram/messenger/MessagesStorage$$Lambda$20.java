package org.telegram.messenger;

import android.util.SparseIntArray;

final /* synthetic */ class MessagesStorage$$Lambda$20 implements Runnable {
    private final MessagesStorage arg$1;
    private final boolean arg$2;
    private final SparseIntArray arg$3;

    MessagesStorage$$Lambda$20(MessagesStorage messagesStorage, boolean z, SparseIntArray sparseIntArray) {
        this.arg$1 = messagesStorage;
        this.arg$2 = z;
        this.arg$3 = sparseIntArray;
    }

    public void run() {
        this.arg$1.lambda$putBlockedUsers$31$MessagesStorage(this.arg$2, this.arg$3);
    }
}
