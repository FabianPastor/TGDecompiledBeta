package org.telegram.messenger;

import android.util.SparseArray;

public final /* synthetic */ class MessagesController$$ExternalSyntheticLambda82 implements Runnable {
    public final /* synthetic */ MessagesController f$0;
    public final /* synthetic */ SparseArray f$1;

    public /* synthetic */ MessagesController$$ExternalSyntheticLambda82(MessagesController messagesController, SparseArray sparseArray) {
        this.f$0 = messagesController;
        this.f$1 = sparseArray;
    }

    public final void run() {
        this.f$0.lambda$didAddedNewTask$54(this.f$1);
    }
}
