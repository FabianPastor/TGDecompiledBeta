package org.telegram.messenger;

import android.util.SparseArray;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$MessagesStorage$uuLM05KdtpS7ns6Dw29DcaBFO24 implements Runnable {
    private final /* synthetic */ MessagesStorage f$0;
    private final /* synthetic */ SparseArray f$1;
    private final /* synthetic */ boolean f$2;

    public /* synthetic */ -$$Lambda$MessagesStorage$uuLM05KdtpS7ns6Dw29DcaBFO24(MessagesStorage messagesStorage, SparseArray sparseArray, boolean z) {
        this.f$0 = messagesStorage;
        this.f$1 = sparseArray;
        this.f$2 = z;
    }

    public final void run() {
        this.f$0.lambda$putChannelViews$118$MessagesStorage(this.f$1, this.f$2);
    }
}
