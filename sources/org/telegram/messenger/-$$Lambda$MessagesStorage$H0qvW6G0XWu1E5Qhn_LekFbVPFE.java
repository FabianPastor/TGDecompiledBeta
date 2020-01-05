package org.telegram.messenger;

import android.util.LongSparseArray;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$MessagesStorage$H0qvW6G0XWu1E5Qhn_LekFbVPFE implements Runnable {
    private final /* synthetic */ MessagesStorage f$0;
    private final /* synthetic */ LongSparseArray f$1;

    public /* synthetic */ -$$Lambda$MessagesStorage$H0qvW6G0XWu1E5Qhn_LekFbVPFE(MessagesStorage messagesStorage, LongSparseArray longSparseArray) {
        this.f$0 = messagesStorage;
        this.f$1 = longSparseArray;
    }

    public final void run() {
        this.f$0.lambda$putWebPages$114$MessagesStorage(this.f$1);
    }
}
