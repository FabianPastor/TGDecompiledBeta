package org.telegram.messenger;

import android.util.SparseArray;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$MessagesController$bHd9VuzZp_-o4GfCjtn5be2-y4I implements Runnable {
    private final /* synthetic */ MessagesController f$0;
    private final /* synthetic */ SparseArray f$1;

    public /* synthetic */ -$$Lambda$MessagesController$bHd9VuzZp_-o4GfCjtn5be2-y4I(MessagesController messagesController, SparseArray sparseArray) {
        this.f$0 = messagesController;
        this.f$1 = sparseArray;
    }

    public final void run() {
        this.f$0.lambda$didAddedNewTask$31$MessagesController(this.f$1);
    }
}
