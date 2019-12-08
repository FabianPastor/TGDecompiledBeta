package org.telegram.ui.Wallet;

import drinkless.org.ton.TonApi.Error;
import org.telegram.messenger.TonController.ErrorCallback;
import org.telegram.ui.Wallet.WalletCreateActivity.AnonymousClass14;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$WalletCreateActivity$14$J-zC5quhSiCjHDcKnMX9EBgJB0w implements ErrorCallback {
    private final /* synthetic */ AnonymousClass14 f$0;

    public /* synthetic */ -$$Lambda$WalletCreateActivity$14$J-zC5quhSiCjHDcKnMX9EBgJB0w(AnonymousClass14 anonymousClass14) {
        this.f$0 = anonymousClass14;
    }

    public final void run(String str, Error error) {
        this.f$0.lambda$doCreate$1$WalletCreateActivity$14(str, error);
    }
}
