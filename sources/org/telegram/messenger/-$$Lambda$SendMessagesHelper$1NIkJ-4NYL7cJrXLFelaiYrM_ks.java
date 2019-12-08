package org.telegram.messenger;

import org.telegram.tgnet.TLRPC.Updates;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$SendMessagesHelper$1NIkJ-4NYL7cJrXLFelaiYrM_ks implements Runnable {
    private final /* synthetic */ SendMessagesHelper f$0;
    private final /* synthetic */ Updates f$1;

    public /* synthetic */ -$$Lambda$SendMessagesHelper$1NIkJ-4NYL7cJrXLFelaiYrM_ks(SendMessagesHelper sendMessagesHelper, Updates updates) {
        this.f$0 = sendMessagesHelper;
        this.f$1 = updates;
    }

    public final void run() {
        this.f$0.lambda$null$29$SendMessagesHelper(this.f$1);
    }
}
