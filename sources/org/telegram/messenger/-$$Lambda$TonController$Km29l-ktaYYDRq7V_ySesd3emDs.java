package org.telegram.messenger;

import drinkless.org.ton.TonApi.GenericAccountState;
import org.telegram.messenger.TonController.AccountStateCallback;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$TonController$Km29l-ktaYYDRq7V_ySesd3emDs implements AccountStateCallback {
    private final /* synthetic */ TonController f$0;
    private final /* synthetic */ GenericAccountState f$1;

    public /* synthetic */ -$$Lambda$TonController$Km29l-ktaYYDRq7V_ySesd3emDs(TonController tonController, GenericAccountState genericAccountState) {
        this.f$0 = tonController;
        this.f$1 = genericAccountState;
    }

    public final void run(GenericAccountState genericAccountState) {
        this.f$0.lambda$runShortPolling$44$TonController(this.f$1, genericAccountState);
    }
}
