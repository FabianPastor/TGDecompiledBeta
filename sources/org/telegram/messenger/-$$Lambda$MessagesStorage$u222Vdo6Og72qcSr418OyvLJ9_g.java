package org.telegram.messenger;

import android.util.SparseArray;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$MessagesStorage$u222Vdo6Og72qcSr418OyvLJ9_g implements Runnable {
    private final /* synthetic */ MessagesStorage f$0;
    private final /* synthetic */ int f$1;
    private final /* synthetic */ SparseArray f$2;

    public /* synthetic */ -$$Lambda$MessagesStorage$u222Vdo6Og72qcSr418OyvLJ9_g(MessagesStorage messagesStorage, int i, SparseArray sparseArray) {
        this.f$0 = messagesStorage;
        this.f$1 = i;
        this.f$2 = sparseArray;
    }

    public final void run() {
        this.f$0.lambda$putChannelAdmins$68$MessagesStorage(this.f$1, this.f$2);
    }
}
