package org.telegram.ui.Wallet;

import org.telegram.messenger.TonController.FeeCallback;
import org.telegram.ui.ActionBar.AlertDialog;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$WalletActionSheet$spBmwL4WiIDV783PfgmnfLmK3Mk implements FeeCallback {
    private final /* synthetic */ WalletActionSheet f$0;
    private final /* synthetic */ AlertDialog f$1;

    public /* synthetic */ -$$Lambda$WalletActionSheet$spBmwL4WiIDV783PfgmnfLmK3Mk(WalletActionSheet walletActionSheet, AlertDialog alertDialog) {
        this.f$0 = walletActionSheet;
        this.f$1 = alertDialog;
    }

    public final void run(long j) {
        this.f$0.lambda$doSend$7$WalletActionSheet(this.f$1, j);
    }
}
