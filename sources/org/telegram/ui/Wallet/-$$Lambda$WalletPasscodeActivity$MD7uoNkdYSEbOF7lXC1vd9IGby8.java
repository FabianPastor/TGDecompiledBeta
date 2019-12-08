package org.telegram.ui.Wallet;

import drinkless.org.ton.TonApi.Error;
import org.telegram.messenger.TonController.ErrorCallback;
import org.telegram.ui.ActionBar.AlertDialog;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$WalletPasscodeActivity$MD7uoNkdYSEbOF7lXC1vd9IGby8 implements ErrorCallback {
    private final /* synthetic */ WalletPasscodeActivity f$0;
    private final /* synthetic */ AlertDialog f$1;

    public /* synthetic */ -$$Lambda$WalletPasscodeActivity$MD7uoNkdYSEbOF7lXC1vd9IGby8(WalletPasscodeActivity walletPasscodeActivity, AlertDialog alertDialog) {
        this.f$0 = walletPasscodeActivity;
        this.f$1 = alertDialog;
    }

    public final void run(String str, Error error) {
        this.f$0.lambda$checkPasscode$1$WalletPasscodeActivity(this.f$1, str, error);
    }
}
