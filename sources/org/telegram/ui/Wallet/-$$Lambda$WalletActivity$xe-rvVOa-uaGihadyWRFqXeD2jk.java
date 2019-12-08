package org.telegram.ui.Wallet;

import android.view.View;
import android.view.View.OnLongClickListener;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$WalletActivity$xe-rvVOa-uaGihadyWRFqXeD2jk implements OnLongClickListener {
    private final /* synthetic */ WalletActivity f$0;
    private final /* synthetic */ String f$1;

    public /* synthetic */ -$$Lambda$WalletActivity$xe-rvVOa-uaGihadyWRFqXeD2jk(WalletActivity walletActivity, String str) {
        this.f$0 = walletActivity;
        this.f$1 = str;
    }

    public final boolean onLongClick(View view) {
        return this.f$0.lambda$showInvoiceSheet$12$WalletActivity(this.f$1, view);
    }
}
