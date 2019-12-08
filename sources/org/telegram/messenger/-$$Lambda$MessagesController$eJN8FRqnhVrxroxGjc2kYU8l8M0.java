package org.telegram.messenger;

import android.util.SparseArray;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$MessagesController$eJN8FRqnhVrxroxGjc2kYU8l8M0 implements Runnable {
    private final /* synthetic */ MessagesController f$0;
    private final /* synthetic */ int f$1;
    private final /* synthetic */ SparseArray f$2;
    private final /* synthetic */ boolean f$3;

    public /* synthetic */ -$$Lambda$MessagesController$eJN8FRqnhVrxroxGjc2kYU8l8M0(MessagesController messagesController, int i, SparseArray sparseArray, boolean z) {
        this.f$0 = messagesController;
        this.f$1 = i;
        this.f$2 = sparseArray;
        this.f$3 = z;
    }

    public final void run() {
        this.f$0.lambda$processLoadedChannelAdmins$24$MessagesController(this.f$1, this.f$2, this.f$3);
    }
}
