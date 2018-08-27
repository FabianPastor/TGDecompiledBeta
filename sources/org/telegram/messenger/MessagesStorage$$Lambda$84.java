package org.telegram.messenger;

import android.util.SparseIntArray;
import org.telegram.messenger.support.SparseLongArray;

final /* synthetic */ class MessagesStorage$$Lambda$84 implements Runnable {
    private final MessagesStorage arg$1;
    private final SparseLongArray arg$2;
    private final SparseLongArray arg$3;
    private final SparseIntArray arg$4;

    MessagesStorage$$Lambda$84(MessagesStorage messagesStorage, SparseLongArray sparseLongArray, SparseLongArray sparseLongArray2, SparseIntArray sparseIntArray) {
        this.arg$1 = messagesStorage;
        this.arg$2 = sparseLongArray;
        this.arg$3 = sparseLongArray2;
        this.arg$4 = sparseIntArray;
    }

    public void run() {
        this.arg$1.lambda$markMessagesAsRead$111$MessagesStorage(this.arg$2, this.arg$3, this.arg$4);
    }
}
