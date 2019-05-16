package org.telegram.ui.ActionBar;

import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$BaseFragment$vXTvtAK8XZpLjv4Env96FSJndOM implements OnDismissListener {
    private final /* synthetic */ BaseFragment f$0;
    private final /* synthetic */ OnDismissListener f$1;

    public /* synthetic */ -$$Lambda$BaseFragment$vXTvtAK8XZpLjv4Env96FSJndOM(BaseFragment baseFragment, OnDismissListener onDismissListener) {
        this.f$0 = baseFragment;
        this.f$1 = onDismissListener;
    }

    public final void onDismiss(DialogInterface dialogInterface) {
        this.f$0.lambda$showDialog$0$BaseFragment(this.f$1, dialogInterface);
    }
}
