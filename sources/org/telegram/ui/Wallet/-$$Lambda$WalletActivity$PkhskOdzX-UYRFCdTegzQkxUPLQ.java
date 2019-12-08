package org.telegram.ui.Wallet;

import java.util.ArrayList;
import org.telegram.messenger.TonController.GetTransactionsCallback;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$WalletActivity$PkhskOdzX-UYRFCdTegzQkxUPLQ implements GetTransactionsCallback {
    private final /* synthetic */ WalletActivity f$0;
    private final /* synthetic */ boolean f$1;

    public /* synthetic */ -$$Lambda$WalletActivity$PkhskOdzX-UYRFCdTegzQkxUPLQ(WalletActivity walletActivity, boolean z) {
        this.f$0 = walletActivity;
        this.f$1 = z;
    }

    public final void run(ArrayList arrayList) {
        this.f$0.lambda$loadTransactions$4$WalletActivity(this.f$1, arrayList);
    }
}
