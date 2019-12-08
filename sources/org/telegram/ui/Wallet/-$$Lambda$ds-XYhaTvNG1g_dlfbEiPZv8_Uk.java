package org.telegram.ui.Wallet;

import org.telegram.ui.Components.ShareAlert.ShareAlertDelegate;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$ds-XYhaTvNG1g_dlfbEiPZv8_Uk implements ShareAlertDelegate {
    private final /* synthetic */ WalletActionActivity f$0;

    public /* synthetic */ -$$Lambda$ds-XYhaTvNG1g_dlfbEiPZv8_Uk(WalletActionActivity walletActionActivity) {
        this.f$0 = walletActionActivity;
    }

    public final void didShare() {
        this.f$0.finishFragment();
    }
}
