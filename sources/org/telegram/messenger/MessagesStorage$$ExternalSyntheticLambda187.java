package org.telegram.messenger;

import androidx.collection.LongSparseArray;

public final /* synthetic */ class MessagesStorage$$ExternalSyntheticLambda187 implements Runnable {
    public final /* synthetic */ MessagesStorage f$0;
    public final /* synthetic */ long f$1;
    public final /* synthetic */ LongSparseArray f$2;

    public /* synthetic */ MessagesStorage$$ExternalSyntheticLambda187(MessagesStorage messagesStorage, long j, LongSparseArray longSparseArray) {
        this.f$0 = messagesStorage;
        this.f$1 = j;
        this.f$2 = longSparseArray;
    }

    public final void run() {
        this.f$0.m959x7ebe6107(this.f$1, this.f$2);
    }
}
