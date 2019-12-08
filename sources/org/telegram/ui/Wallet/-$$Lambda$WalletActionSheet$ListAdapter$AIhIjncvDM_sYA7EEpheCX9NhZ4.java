package org.telegram.ui.Wallet;

import android.view.KeyEvent;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$WalletActionSheet$ListAdapter$AIhIjncvDM_sYA7EEpheCX9NhZ4 implements OnEditorActionListener {
    private final /* synthetic */ ListAdapter f$0;

    public /* synthetic */ -$$Lambda$WalletActionSheet$ListAdapter$AIhIjncvDM_sYA7EEpheCX9NhZ4(ListAdapter listAdapter) {
        this.f$0 = listAdapter;
    }

    public final boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
        return this.f$0.lambda$onCreateViewHolder$0$WalletActionSheet$ListAdapter(textView, i, keyEvent);
    }
}
