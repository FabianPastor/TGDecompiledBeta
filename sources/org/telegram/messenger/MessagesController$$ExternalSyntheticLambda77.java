package org.telegram.messenger;

import androidx.collection.LongSparseArray;

public final /* synthetic */ class MessagesController$$ExternalSyntheticLambda77 implements Runnable {
    public final /* synthetic */ MessagesController f$0;
    public final /* synthetic */ long f$1;
    public final /* synthetic */ LongSparseArray f$2;
    public final /* synthetic */ boolean f$3;

    public /* synthetic */ MessagesController$$ExternalSyntheticLambda77(MessagesController messagesController, long j, LongSparseArray longSparseArray, boolean z) {
        this.f$0 = messagesController;
        this.f$1 = j;
        this.f$2 = longSparseArray;
        this.f$3 = z;
    }

    public final void run() {
        this.f$0.lambda$processLoadedChannelAdmins$45(this.f$1, this.f$2, this.f$3);
    }
}
