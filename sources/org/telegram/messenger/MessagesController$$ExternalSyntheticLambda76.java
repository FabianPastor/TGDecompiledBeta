package org.telegram.messenger;

import android.util.SparseArray;

public final /* synthetic */ class MessagesController$$ExternalSyntheticLambda76 implements Runnable {
    public final /* synthetic */ MessagesController f$0;
    public final /* synthetic */ long f$1;
    public final /* synthetic */ SparseArray f$2;

    public /* synthetic */ MessagesController$$ExternalSyntheticLambda76(MessagesController messagesController, long j, SparseArray sparseArray) {
        this.f$0 = messagesController;
        this.f$1 = j;
        this.f$2 = sparseArray;
    }

    public final void run() {
        this.f$0.lambda$didAddedNewTask$61(this.f$1, this.f$2);
    }
}
