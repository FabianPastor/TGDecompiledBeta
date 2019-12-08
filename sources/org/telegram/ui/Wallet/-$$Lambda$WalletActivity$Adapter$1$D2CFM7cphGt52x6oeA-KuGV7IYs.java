package org.telegram.ui.Wallet;

import android.view.View;
import android.view.View.OnLongClickListener;
import android.widget.TextView;
import org.telegram.ui.Wallet.WalletActivity.Adapter.AnonymousClass1;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$WalletActivity$Adapter$1$D2CFM7cphGt52x6oeA-KuGV7IYs implements OnLongClickListener {
    private final /* synthetic */ AnonymousClass1 f$0;
    private final /* synthetic */ TextView f$1;

    public /* synthetic */ -$$Lambda$WalletActivity$Adapter$1$D2CFM7cphGt52x6oeA-KuGV7IYs(AnonymousClass1 anonymousClass1, TextView textView) {
        this.f$0 = anonymousClass1;
        this.f$1 = textView;
    }

    public final boolean onLongClick(View view) {
        return this.f$0.lambda$onReceivePressed$3$WalletActivity$Adapter$1(this.f$1, view);
    }
}
