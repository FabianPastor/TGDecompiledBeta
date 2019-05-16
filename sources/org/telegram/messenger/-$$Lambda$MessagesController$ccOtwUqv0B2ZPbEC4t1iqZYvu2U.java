package org.telegram.messenger;

import org.telegram.messenger.MessagesStorage.IntCallback;
import org.telegram.tgnet.TLRPC.Updates;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$MessagesController$ccOtwUqv0B2ZPbEC4t1iqZYvu2U implements Runnable {
    private final /* synthetic */ IntCallback f$0;
    private final /* synthetic */ Updates f$1;

    public /* synthetic */ -$$Lambda$MessagesController$ccOtwUqv0B2ZPbEC4t1iqZYvu2U(IntCallback intCallback, Updates updates) {
        this.f$0 = intCallback;
        this.f$1 = updates;
    }

    public final void run() {
        MessagesController.lambda$null$162(this.f$0, this.f$1);
    }
}
