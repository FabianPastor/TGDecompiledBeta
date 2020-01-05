package org.telegram.messenger;

import org.telegram.messenger.TonController.AccountStateCallback;
import org.telegram.messenger.TonController.TonLibCallback;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$TonController$lFlasxIPj0pe3vPrwkuvuEnSFGk implements TonLibCallback {
    private final /* synthetic */ TonController f$0;
    private final /* synthetic */ AccountStateCallback f$1;

    public /* synthetic */ -$$Lambda$TonController$lFlasxIPj0pe3vPrwkuvuEnSFGk(TonController tonController, AccountStateCallback accountStateCallback) {
        this.f$0 = tonController;
        this.f$1 = accountStateCallback;
    }

    public final void run(Object obj) {
        this.f$0.lambda$getAccountState$20$TonController(this.f$1, obj);
    }
}
