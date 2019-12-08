package org.telegram.messenger;

import android.util.SparseArray;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$MessagesController$LqVXXvpbSugUO7Ph2v9tZNwc3FU implements Runnable {
    private final /* synthetic */ MessagesController f$0;
    private final /* synthetic */ SparseArray f$1;

    public /* synthetic */ -$$Lambda$MessagesController$LqVXXvpbSugUO7Ph2v9tZNwc3FU(MessagesController messagesController, SparseArray sparseArray) {
        this.f$0 = messagesController;
        this.f$1 = sparseArray;
    }

    public final void run() {
        this.f$0.lambda$didAddedNewTask$39$MessagesController(this.f$1);
    }
}
