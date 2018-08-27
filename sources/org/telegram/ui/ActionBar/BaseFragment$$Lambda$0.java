package org.telegram.ui.ActionBar;

import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;

final /* synthetic */ class BaseFragment$$Lambda$0 implements OnDismissListener {
    private final BaseFragment arg$1;
    private final OnDismissListener arg$2;

    BaseFragment$$Lambda$0(BaseFragment baseFragment, OnDismissListener onDismissListener) {
        this.arg$1 = baseFragment;
        this.arg$2 = onDismissListener;
    }

    public void onDismiss(DialogInterface dialogInterface) {
        this.arg$1.lambda$showDialog$0$BaseFragment(this.arg$2, dialogInterface);
    }
}
