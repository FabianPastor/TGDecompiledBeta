package org.telegram.ui.Wallet;

import org.telegram.messenger.TonController.WordsCallback;
import org.telegram.ui.ActionBar.AlertDialog;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$WalletPasscodeActivity$6t39g59yWrccW6Nn-Y0ftETkiCA implements WordsCallback {
    private final /* synthetic */ WalletPasscodeActivity f$0;
    private final /* synthetic */ AlertDialog f$1;

    public /* synthetic */ -$$Lambda$WalletPasscodeActivity$6t39g59yWrccW6Nn-Y0ftETkiCA(WalletPasscodeActivity walletPasscodeActivity, AlertDialog alertDialog) {
        this.f$0 = walletPasscodeActivity;
        this.f$1 = alertDialog;
    }

    public final void run(String[] strArr) {
        this.f$0.lambda$checkPasscode$3$WalletPasscodeActivity(this.f$1, strArr);
    }
}
