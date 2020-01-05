package org.telegram.messenger;

import org.telegram.messenger.TonController.AccountStateCallback;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$TonController$wnGpkvpHsIHyzkpFHXBflXm6OU8 implements Runnable {
    private final /* synthetic */ TonController f$0;
    private final /* synthetic */ AccountStateCallback f$1;
    private final /* synthetic */ Object f$2;

    public /* synthetic */ -$$Lambda$TonController$wnGpkvpHsIHyzkpFHXBflXm6OU8(TonController tonController, AccountStateCallback accountStateCallback, Object obj) {
        this.f$0 = tonController;
        this.f$1 = accountStateCallback;
        this.f$2 = obj;
    }

    public final void run() {
        this.f$0.lambda$null$20$TonController(this.f$1, this.f$2);
    }
}
