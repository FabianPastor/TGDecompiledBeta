package org.telegram.messenger;

import android.util.SparseIntArray;
import org.telegram.messenger.support.SparseLongArray;

public final /* synthetic */ class MessagesStorage$$ExternalSyntheticLambda141 implements Runnable {
    public final /* synthetic */ MessagesStorage f$0;
    public final /* synthetic */ SparseLongArray f$1;
    public final /* synthetic */ SparseLongArray f$2;
    public final /* synthetic */ SparseIntArray f$3;

    public /* synthetic */ MessagesStorage$$ExternalSyntheticLambda141(MessagesStorage messagesStorage, SparseLongArray sparseLongArray, SparseLongArray sparseLongArray2, SparseIntArray sparseIntArray) {
        this.f$0 = messagesStorage;
        this.f$1 = sparseLongArray;
        this.f$2 = sparseLongArray2;
        this.f$3 = sparseIntArray;
    }

    public final void run() {
        this.f$0.lambda$markMessagesAsRead$153(this.f$1, this.f$2, this.f$3);
    }
}
