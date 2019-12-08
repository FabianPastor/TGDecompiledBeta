package org.telegram.ui.Wallet;

import drinkless.org.ton.TonApi.Error;
import org.telegram.messenger.TonController.ErrorCallback;
import org.telegram.ui.ActionBar.AlertDialog;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$WalletCreateActivity$4kb5fBbPHQtOLTgjF6Qhv5-eYwM implements ErrorCallback {
    private final /* synthetic */ WalletCreateActivity f$0;
    private final /* synthetic */ AlertDialog f$1;

    public /* synthetic */ -$$Lambda$WalletCreateActivity$4kb5fBbPHQtOLTgjF6Qhv5-eYwM(WalletCreateActivity walletCreateActivity, AlertDialog alertDialog) {
        this.f$0 = walletCreateActivity;
        this.f$1 = alertDialog;
    }

    public final void run(String str, Error error) {
        this.f$0.lambda$doExport$16$WalletCreateActivity(this.f$1, str, error);
    }
}
