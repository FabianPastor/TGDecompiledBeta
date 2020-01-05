package org.telegram.messenger;

import org.telegram.messenger.TonController.BooleanCallback;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$TonController$4dT3xvzVSa-pPW-MT9gNBpw3wns implements Runnable {
    private final /* synthetic */ TonController f$0;
    private final /* synthetic */ BooleanCallback f$1;

    public /* synthetic */ -$$Lambda$TonController$4dT3xvzVSa-pPW-MT9gNBpw3wns(TonController tonController, BooleanCallback booleanCallback) {
        this.f$0 = tonController;
        this.f$1 = booleanCallback;
    }

    public final void run() {
        this.f$0.lambda$isKeyStoreInvalidated$3$TonController(this.f$1);
    }
}
