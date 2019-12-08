package org.telegram.ui.Wallet;

import drinkless.org.ton.TonApi.GenericAccountState;
import org.telegram.messenger.TonController.AccountStateCallback;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$WalletActivity$lzOFOMId1vEyD4Ft4kkxXA8SqEU implements AccountStateCallback {
    private final /* synthetic */ WalletActivity f$0;

    public /* synthetic */ -$$Lambda$WalletActivity$lzOFOMId1vEyD4Ft4kkxXA8SqEU(WalletActivity walletActivity) {
        this.f$0 = walletActivity;
    }

    public final void run(GenericAccountState genericAccountState) {
        this.f$0.lambda$loadAccountState$8$WalletActivity(genericAccountState);
    }
}
