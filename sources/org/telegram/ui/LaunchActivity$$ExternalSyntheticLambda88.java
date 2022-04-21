package org.telegram.ui;

import android.content.DialogInterface;
import org.telegram.ui.ActionBar.BaseFragment;

public final /* synthetic */ class LaunchActivity$$ExternalSyntheticLambda88 implements DialogInterface.OnClickListener {
    public final /* synthetic */ String f$0;
    public final /* synthetic */ String f$1;
    public final /* synthetic */ BaseFragment f$2;

    public /* synthetic */ LaunchActivity$$ExternalSyntheticLambda88(String str, String str2, BaseFragment baseFragment) {
        this.f$0 = str;
        this.f$1 = str2;
        this.f$2 = baseFragment;
    }

    public final void onClick(DialogInterface dialogInterface, int i) {
        LaunchActivity.lambda$handleIntent$21(this.f$0, this.f$1, this.f$2, dialogInterface, i);
    }
}
