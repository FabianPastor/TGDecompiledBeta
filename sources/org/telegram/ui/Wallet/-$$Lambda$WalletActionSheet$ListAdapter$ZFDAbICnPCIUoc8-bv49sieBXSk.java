package org.telegram.ui.Wallet;

import android.view.KeyEvent;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$WalletActionSheet$ListAdapter$ZFDAbICnPCIUoc8-bv49sieBXSk implements OnEditorActionListener {
    private final /* synthetic */ ListAdapter f$0;

    public /* synthetic */ -$$Lambda$WalletActionSheet$ListAdapter$ZFDAbICnPCIUoc8-bv49sieBXSk(ListAdapter listAdapter) {
        this.f$0 = listAdapter;
    }

    public final boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
        return this.f$0.lambda$createView$0$WalletActionSheet$ListAdapter(textView, i, keyEvent);
    }
}
