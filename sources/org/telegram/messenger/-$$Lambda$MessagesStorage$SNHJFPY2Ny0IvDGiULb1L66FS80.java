package org.telegram.messenger;

import android.util.LongSparseArray;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$MessagesStorage$SNHJFPY2Ny0IvDGiULb1L66FS80 implements Runnable {
    private final /* synthetic */ MessagesStorage f$0;
    private final /* synthetic */ LongSparseArray f$1;

    public /* synthetic */ -$$Lambda$MessagesStorage$SNHJFPY2Ny0IvDGiULb1L66FS80(MessagesStorage messagesStorage, LongSparseArray longSparseArray) {
        this.f$0 = messagesStorage;
        this.f$1 = longSparseArray;
    }

    public final void run() {
        this.f$0.lambda$putWebPages$118$MessagesStorage(this.f$1);
    }
}
