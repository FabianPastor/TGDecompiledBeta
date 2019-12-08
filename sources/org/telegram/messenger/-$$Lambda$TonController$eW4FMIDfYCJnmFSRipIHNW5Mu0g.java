package org.telegram.messenger;

import org.telegram.messenger.TonController.TonLibCallback;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$TonController$eW4FMIDfYCJnmFSRipIHNW5Mu0g implements TonLibCallback {
    private final /* synthetic */ TonController f$0;
    private final /* synthetic */ boolean f$1;
    private final /* synthetic */ Runnable f$2;

    public /* synthetic */ -$$Lambda$TonController$eW4FMIDfYCJnmFSRipIHNW5Mu0g(TonController tonController, boolean z, Runnable runnable) {
        this.f$0 = tonController;
        this.f$1 = z;
        this.f$2 = runnable;
    }

    public final void run(Object obj) {
        this.f$0.lambda$getTransactions$17$TonController(this.f$1, this.f$2, obj);
    }
}
