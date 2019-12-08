package org.telegram.ui.Wallet;

import android.view.KeyEvent;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$WalletActionActivity$ListAdapter$f0Hi0Z6Xpx_UzAvAcpeR0yOl87E implements OnEditorActionListener {
    private final /* synthetic */ ListAdapter f$0;

    public /* synthetic */ -$$Lambda$WalletActionActivity$ListAdapter$f0Hi0Z6Xpx_UzAvAcpeR0yOl87E(ListAdapter listAdapter) {
        this.f$0 = listAdapter;
    }

    public final boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
        return this.f$0.lambda$onCreateViewHolder$0$WalletActionActivity$ListAdapter(textView, i, keyEvent);
    }
}
