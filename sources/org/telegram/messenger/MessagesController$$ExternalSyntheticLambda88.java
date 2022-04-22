package org.telegram.messenger;

import android.util.SparseBooleanArray;

public final /* synthetic */ class MessagesController$$ExternalSyntheticLambda88 implements Runnable {
    public final /* synthetic */ MessagesController f$0;
    public final /* synthetic */ SparseBooleanArray f$1;
    public final /* synthetic */ long f$2;

    public /* synthetic */ MessagesController$$ExternalSyntheticLambda88(MessagesController messagesController, SparseBooleanArray sparseBooleanArray, long j) {
        this.f$0 = messagesController;
        this.f$1 = sparseBooleanArray;
        this.f$2 = j;
    }

    public final void run() {
        this.f$0.lambda$checkUnreadReactions$327(this.f$1, this.f$2);
    }
}
