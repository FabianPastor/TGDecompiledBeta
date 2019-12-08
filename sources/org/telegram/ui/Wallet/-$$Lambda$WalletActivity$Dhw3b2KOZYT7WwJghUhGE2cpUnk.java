package org.telegram.ui.Wallet;

import org.telegram.ui.ActionBar.ActionBarMenuItem.ActionBarMenuItemDelegate;
import org.telegram.ui.ActionBar.BottomSheet.Builder;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$WalletActivity$Dhw3b2KOZYT7WwJghUhGE2cpUnk implements ActionBarMenuItemDelegate {
    private final /* synthetic */ WalletActivity f$0;
    private final /* synthetic */ Builder f$1;
    private final /* synthetic */ String f$2;

    public /* synthetic */ -$$Lambda$WalletActivity$Dhw3b2KOZYT7WwJghUhGE2cpUnk(WalletActivity walletActivity, Builder builder, String str) {
        this.f$0 = walletActivity;
        this.f$1 = builder;
        this.f$2 = str;
    }

    public final void onItemClick(int i) {
        this.f$0.lambda$showInvoiceSheet$9$WalletActivity(this.f$1, this.f$2, i);
    }
}
