package org.telegram.messenger;

import org.telegram.messenger.MessagesStorage.BooleanCallback;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$TonController$vCQCM8brXT-96q69kGpxplSzY7s implements Runnable {
    private final /* synthetic */ TonController f$0;
    private final /* synthetic */ BooleanCallback f$1;

    public /* synthetic */ -$$Lambda$TonController$vCQCM8brXT-96q69kGpxplSzY7s(TonController tonController, BooleanCallback booleanCallback) {
        this.f$0 = tonController;
        this.f$1 = booleanCallback;
    }

    public final void run() {
        this.f$0.lambda$isKeyStoreInvalidated$5$TonController(this.f$1);
    }
}
