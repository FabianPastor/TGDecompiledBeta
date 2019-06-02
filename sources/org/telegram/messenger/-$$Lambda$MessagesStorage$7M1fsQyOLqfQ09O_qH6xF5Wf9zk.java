package org.telegram.messenger;

import android.util.SparseIntArray;
import org.telegram.messenger.support.SparseLongArray;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$MessagesStorage$7M1fsQyOLqfQ09O_qH6xF5Wf9zk implements Runnable {
    private final /* synthetic */ MessagesStorage f$0;
    private final /* synthetic */ SparseLongArray f$1;
    private final /* synthetic */ SparseLongArray f$2;
    private final /* synthetic */ SparseIntArray f$3;

    public /* synthetic */ -$$Lambda$MessagesStorage$7M1fsQyOLqfQ09O_qH6xF5Wf9zk(MessagesStorage messagesStorage, SparseLongArray sparseLongArray, SparseLongArray sparseLongArray2, SparseIntArray sparseIntArray) {
        this.f$0 = messagesStorage;
        this.f$1 = sparseLongArray;
        this.f$2 = sparseLongArray2;
        this.f$3 = sparseIntArray;
    }

    public final void run() {
        this.f$0.lambda$markMessagesAsRead$130$MessagesStorage(this.f$1, this.f$2, this.f$3);
    }
}
