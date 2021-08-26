package org.telegram.ui.ActionBar;

import android.content.DialogInterface;

public final /* synthetic */ class BaseFragment$1$$ExternalSyntheticLambda0 implements DialogInterface.OnDismissListener {
    public final /* synthetic */ BaseFragment f$0;

    public /* synthetic */ BaseFragment$1$$ExternalSyntheticLambda0(BaseFragment baseFragment) {
        this.f$0 = baseFragment;
    }

    public final void onDismiss(DialogInterface dialogInterface) {
        this.f$0.onFragmentDestroy();
    }
}
