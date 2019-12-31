package org.telegram.messenger;

import org.telegram.messenger.TonController.TonLibCallback;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$TonController$BJplM7TBHaoagaFxa2S8jmt1XHQ implements TonLibCallback {
    private final /* synthetic */ TonController f$0;
    private final /* synthetic */ boolean f$1;
    private final /* synthetic */ Runnable f$2;

    public /* synthetic */ -$$Lambda$TonController$BJplM7TBHaoagaFxa2S8jmt1XHQ(TonController tonController, boolean z, Runnable runnable) {
        this.f$0 = tonController;
        this.f$1 = z;
        this.f$2 = runnable;
    }

    public final void run(Object obj) {
        this.f$0.lambda$getTransactions$15$TonController(this.f$1, this.f$2, obj);
    }
}
