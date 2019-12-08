package org.telegram.messenger;

import android.util.LongSparseArray;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$MessagesStorage$ZCLASSNAMEBg__blruiuNfZet6xS5RP-A implements Runnable {
    private final /* synthetic */ MessagesStorage f$0;
    private final /* synthetic */ LongSparseArray f$1;

    public /* synthetic */ -$$Lambda$MessagesStorage$ZCLASSNAMEBg__blruiuNfZet6xS5RP-A(MessagesStorage messagesStorage, LongSparseArray longSparseArray) {
        this.f$0 = messagesStorage;
        this.f$1 = longSparseArray;
    }

    public final void run() {
        this.f$0.lambda$putWebPages$116$MessagesStorage(this.f$1);
    }
}
