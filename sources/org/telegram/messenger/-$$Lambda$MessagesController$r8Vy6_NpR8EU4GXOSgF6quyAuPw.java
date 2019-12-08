package org.telegram.messenger;

import android.util.SparseArray;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$MessagesController$r8Vy6_NpR8EU4GXOSgF6quyAuPw implements Runnable {
    private final /* synthetic */ MessagesController f$0;
    private final /* synthetic */ SparseArray f$1;

    public /* synthetic */ -$$Lambda$MessagesController$r8Vy6_NpR8EU4GXOSgF6quyAuPw(MessagesController messagesController, SparseArray sparseArray) {
        this.f$0 = messagesController;
        this.f$1 = sparseArray;
    }

    public final void run() {
        this.f$0.lambda$didAddedNewTask$33$MessagesController(this.f$1);
    }
}
