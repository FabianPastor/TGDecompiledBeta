package org.telegram.ui.Wallet;

import drinkless.org.ton.TonApi.Error;
import org.telegram.messenger.TonController.ErrorCallback;
import org.telegram.ui.ActionBar.AlertDialog;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$WalletPasscodeActivity$5HBHbcbDcSEutsBdkaH9R8Y28AE implements ErrorCallback {
    private final /* synthetic */ WalletPasscodeActivity f$0;
    private final /* synthetic */ AlertDialog f$1;

    public /* synthetic */ -$$Lambda$WalletPasscodeActivity$5HBHbcbDcSEutsBdkaH9R8Y28AE(WalletPasscodeActivity walletPasscodeActivity, AlertDialog alertDialog) {
        this.f$0 = walletPasscodeActivity;
        this.f$1 = alertDialog;
    }

    public final void run(String str, Error error) {
        this.f$0.lambda$trySendGrams$11$WalletPasscodeActivity(this.f$1, str, error);
    }
}