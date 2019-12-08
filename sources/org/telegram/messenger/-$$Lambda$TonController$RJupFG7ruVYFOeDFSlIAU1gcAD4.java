package org.telegram.messenger;

import drinkless.org.ton.TonApi.GenericAccountState;
import org.telegram.messenger.TonController.AccountStateCallback;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$TonController$RJupFG7ruVYFOeDFSlIAU1gcAD4 implements AccountStateCallback {
    private final /* synthetic */ TonController f$0;

    public /* synthetic */ -$$Lambda$TonController$RJupFG7ruVYFOeDFSlIAU1gcAD4(TonController tonController) {
        this.f$0 = tonController;
    }

    public final void run(GenericAccountState genericAccountState) {
        this.f$0.lambda$runShortPolling$36$TonController(genericAccountState);
    }
}
