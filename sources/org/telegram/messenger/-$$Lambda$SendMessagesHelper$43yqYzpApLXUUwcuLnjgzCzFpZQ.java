package org.telegram.messenger;

import org.telegram.tgnet.TLRPC.Updates;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$SendMessagesHelper$43yqYzpApLXUUwcuLnjgzCzFpZQ implements Runnable {
    private final /* synthetic */ SendMessagesHelper f$0;
    private final /* synthetic */ Updates f$1;

    public /* synthetic */ -$$Lambda$SendMessagesHelper$43yqYzpApLXUUwcuLnjgzCzFpZQ(SendMessagesHelper sendMessagesHelper, Updates updates) {
        this.f$0 = sendMessagesHelper;
        this.f$1 = updates;
    }

    public final void run() {
        this.f$0.lambda$null$42$SendMessagesHelper(this.f$1);
    }
}
