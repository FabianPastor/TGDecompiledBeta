package org.telegram.ui.Wallet;

import org.telegram.ui.ActionBar.ActionBarMenuItem.ActionBarMenuItemDelegate;
import org.telegram.ui.ActionBar.BottomSheet.Builder;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$WalletActivity$RhSn0vRs2B8-NN4eTvkKIjlDheY implements ActionBarMenuItemDelegate {
    private final /* synthetic */ WalletActivity f$0;
    private final /* synthetic */ Builder f$1;
    private final /* synthetic */ String f$2;

    public /* synthetic */ -$$Lambda$WalletActivity$RhSn0vRs2B8-NN4eTvkKIjlDheY(WalletActivity walletActivity, Builder builder, String str) {
        this.f$0 = walletActivity;
        this.f$1 = builder;
        this.f$2 = str;
    }

    public final void onItemClick(int i) {
        this.f$0.lambda$showInvoiceSheet$10$WalletActivity(this.f$1, this.f$2, i);
    }
}
