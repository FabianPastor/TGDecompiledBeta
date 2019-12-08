package org.telegram.messenger;

import org.telegram.tgnet.TLRPC.Message;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$SendMessagesHelper$qWBM-B1BiqXoChsV1qELy1NJl9k implements Runnable {
    private final /* synthetic */ SendMessagesHelper f$0;
    private final /* synthetic */ Message f$1;
    private final /* synthetic */ int f$2;
    private final /* synthetic */ long f$3;
    private final /* synthetic */ int f$4;
    private final /* synthetic */ boolean f$5;

    public /* synthetic */ -$$Lambda$SendMessagesHelper$qWBM-B1BiqXoChsV1qELy1NJl9k(SendMessagesHelper sendMessagesHelper, Message message, int i, long j, int i2, boolean z) {
        this.f$0 = sendMessagesHelper;
        this.f$1 = message;
        this.f$2 = i;
        this.f$3 = j;
        this.f$4 = i2;
        this.f$5 = z;
    }

    public final void run() {
        this.f$0.lambda$null$29$SendMessagesHelper(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5);
    }
}
