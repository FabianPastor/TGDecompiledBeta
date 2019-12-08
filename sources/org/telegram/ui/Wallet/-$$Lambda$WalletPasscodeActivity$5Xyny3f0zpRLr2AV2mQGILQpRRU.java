package org.telegram.ui.Wallet;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import drinkless.org.ton.TonApi.InputKey;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$WalletPasscodeActivity$5Xyny3f0zpRLr2AV2mQGILQpRRU implements OnClickListener {
    private final /* synthetic */ WalletPasscodeActivity f$0;
    private final /* synthetic */ String f$1;
    private final /* synthetic */ InputKey f$2;

    public /* synthetic */ -$$Lambda$WalletPasscodeActivity$5Xyny3f0zpRLr2AV2mQGILQpRRU(WalletPasscodeActivity walletPasscodeActivity, String str, InputKey inputKey) {
        this.f$0 = walletPasscodeActivity;
        this.f$1 = str;
        this.f$2 = inputKey;
    }

    public final void onClick(DialogInterface dialogInterface, int i) {
        this.f$0.lambda$null$7$WalletPasscodeActivity(this.f$1, this.f$2, dialogInterface, i);
    }
}
