package org.telegram.messenger;

import org.telegram.messenger.MessagesStorage.IntCallback;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$MessagesStorage$ldjdkm--KTEvj7b3Kw6L1hvd8lo implements Runnable {
    private final /* synthetic */ IntCallback f$0;
    private final /* synthetic */ int f$1;

    public /* synthetic */ -$$Lambda$MessagesStorage$ldjdkm--KTEvj7b3Kw6L1hvd8lo(IntCallback intCallback, int i) {
        this.f$0 = intCallback;
        this.f$1 = i;
    }

    public final void run() {
        this.f$0.run(this.f$1);
    }
}