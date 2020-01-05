package org.telegram.messenger;

import org.telegram.messenger.TonController.FeeCallback;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$TonController$gx970w2d8N8gkzu44QDyYZdfNUY implements Runnable {
    private final /* synthetic */ FeeCallback f$0;
    private final /* synthetic */ long f$1;

    public /* synthetic */ -$$Lambda$TonController$gx970w2d8N8gkzu44QDyYZdfNUY(FeeCallback feeCallback, long j) {
        this.f$0 = feeCallback;
        this.f$1 = j;
    }

    public final void run() {
        this.f$0.run(this.f$1);
    }
}
