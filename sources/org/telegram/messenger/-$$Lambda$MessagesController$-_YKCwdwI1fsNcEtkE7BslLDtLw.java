package org.telegram.messenger;

import org.telegram.messenger.MessagesStorage.IntCallback;
import org.telegram.tgnet.TLRPC.Updates;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$MessagesController$-_YKCwdwI1fsNcEtkE7BslLDtLw implements Runnable {
    private final /* synthetic */ IntCallback f$0;
    private final /* synthetic */ Updates f$1;

    public /* synthetic */ -$$Lambda$MessagesController$-_YKCwdwI1fsNcEtkE7BslLDtLw(IntCallback intCallback, Updates updates) {
        this.f$0 = intCallback;
        this.f$1 = updates;
    }

    public final void run() {
        MessagesController.lambda$null$177(this.f$0, this.f$1);
    }
}
