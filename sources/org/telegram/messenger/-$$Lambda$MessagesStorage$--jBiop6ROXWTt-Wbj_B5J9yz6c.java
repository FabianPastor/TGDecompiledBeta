package org.telegram.messenger;

import android.util.SparseArray;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$MessagesStorage$--jBiop6ROXWTt-Wbj_B5J9yz6c implements Runnable {
    private final /* synthetic */ MessagesStorage f$0;
    private final /* synthetic */ int f$1;
    private final /* synthetic */ SparseArray f$2;

    public /* synthetic */ -$$Lambda$MessagesStorage$--jBiop6ROXWTt-Wbj_B5J9yz6c(MessagesStorage messagesStorage, int i, SparseArray sparseArray) {
        this.f$0 = messagesStorage;
        this.f$1 = i;
        this.f$2 = sparseArray;
    }

    public final void run() {
        this.f$0.lambda$putChannelAdmins$66$MessagesStorage(this.f$1, this.f$2);
    }
}
