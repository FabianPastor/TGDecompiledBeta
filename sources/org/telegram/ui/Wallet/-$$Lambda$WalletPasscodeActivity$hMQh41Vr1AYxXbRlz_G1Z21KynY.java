package org.telegram.ui.Wallet;

import drinkless.org.ton.TonApi.InputKey;
import org.telegram.messenger.TonController.DangerousCallback;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$WalletPasscodeActivity$hMQh41Vr1AYxXbRlz_G1Z21KynY implements DangerousCallback {
    private final /* synthetic */ WalletPasscodeActivity f$0;
    private final /* synthetic */ String f$1;

    public /* synthetic */ -$$Lambda$WalletPasscodeActivity$hMQh41Vr1AYxXbRlz_G1Z21KynY(WalletPasscodeActivity walletPasscodeActivity, String str) {
        this.f$0 = walletPasscodeActivity;
        this.f$1 = str;
    }

    public final void run(InputKey inputKey) {
        this.f$0.lambda$trySendGrams$10$WalletPasscodeActivity(this.f$1, inputKey);
    }
}
