package org.telegram.ui.Wallet;

import java.util.ArrayList;
import org.telegram.messenger.TonController.GetTransactionsCallback;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$WalletActivity$occZ8oAPj7HwE4RGCYXQ49eXwLs implements GetTransactionsCallback {
    private final /* synthetic */ WalletActivity f$0;
    private final /* synthetic */ boolean f$1;

    public /* synthetic */ -$$Lambda$WalletActivity$occZ8oAPj7HwE4RGCYXQ49eXwLs(WalletActivity walletActivity, boolean z) {
        this.f$0 = walletActivity;
        this.f$1 = z;
    }

    public final void run(ArrayList arrayList) {
        this.f$0.lambda$loadTransactions$7$WalletActivity(this.f$1, arrayList);
    }
}
