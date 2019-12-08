package org.telegram.messenger;

import drinkless.org.ton.TonApi.GenericAccountState;
import org.telegram.messenger.TonController.AccountStateCallback;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$TonController$4sUFBUpjSKVJQ64xFw7yS9hJtZg implements AccountStateCallback {
    private final /* synthetic */ TonController f$0;

    public /* synthetic */ -$$Lambda$TonController$4sUFBUpjSKVJQ64xFw7yS9hJtZg(TonController tonController) {
        this.f$0 = tonController;
    }

    public final void run(GenericAccountState genericAccountState) {
        this.f$0.lambda$preloadWallet$19$TonController(genericAccountState);
    }
}
