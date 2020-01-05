package org.telegram.messenger;

import org.telegram.messenger.TonController.ErrorCallback;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$TonController$-Sq-PWfPKNVqcUgyq-JbOy4U1qk implements Runnable {
    private final /* synthetic */ TonController f$0;
    private final /* synthetic */ ErrorCallback f$1;
    private final /* synthetic */ Object f$2;

    public /* synthetic */ -$$Lambda$TonController$-Sq-PWfPKNVqcUgyq-JbOy4U1qk(TonController tonController, ErrorCallback errorCallback, Object obj) {
        this.f$0 = tonController;
        this.f$1 = errorCallback;
        this.f$2 = obj;
    }

    public final void run() {
        this.f$0.lambda$null$6$TonController(this.f$1, this.f$2);
    }
}
