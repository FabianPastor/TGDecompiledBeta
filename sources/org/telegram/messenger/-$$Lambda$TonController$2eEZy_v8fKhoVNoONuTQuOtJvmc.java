package org.telegram.messenger;

import org.telegram.messenger.TonController.ErrorCallback;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$TonController$2eEZy_v8fKhoVNoONuTQuOtJvmc implements Runnable {
    private final /* synthetic */ TonController f$0;
    private final /* synthetic */ ErrorCallback f$1;
    private final /* synthetic */ Object f$2;

    public /* synthetic */ -$$Lambda$TonController$2eEZy_v8fKhoVNoONuTQuOtJvmc(TonController tonController, ErrorCallback errorCallback, Object obj) {
        this.f$0 = tonController;
        this.f$1 = errorCallback;
        this.f$2 = obj;
    }

    public final void run() {
        this.f$0.lambda$null$38$TonController(this.f$1, this.f$2);
    }
}