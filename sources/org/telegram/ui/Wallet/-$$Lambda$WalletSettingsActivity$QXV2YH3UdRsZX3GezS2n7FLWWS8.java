package org.telegram.ui.Wallet;

import drinkless.org.ton.TonApi.Error;
import org.telegram.messenger.TonController.ErrorCallback;
import org.telegram.ui.ActionBar.AlertDialog;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$WalletSettingsActivity$QXV2YH3UdRsZX3GezS2n7FLWWS8 implements ErrorCallback {
    private final /* synthetic */ WalletSettingsActivity f$0;
    private final /* synthetic */ AlertDialog f$1;

    public /* synthetic */ -$$Lambda$WalletSettingsActivity$QXV2YH3UdRsZX3GezS2n7FLWWS8(WalletSettingsActivity walletSettingsActivity, AlertDialog alertDialog) {
        this.f$0 = walletSettingsActivity;
        this.f$1 = alertDialog;
    }

    public final void run(String str, Error error) {
        this.f$0.lambda$doExport$3$WalletSettingsActivity(this.f$1, str, error);
    }
}
