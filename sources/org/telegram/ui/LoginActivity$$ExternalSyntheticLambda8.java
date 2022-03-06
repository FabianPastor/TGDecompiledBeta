package org.telegram.ui;

import android.content.DialogInterface;
import org.telegram.ui.ActionBar.BaseFragment;

public final /* synthetic */ class LoginActivity$$ExternalSyntheticLambda8 implements DialogInterface.OnClickListener {
    public final /* synthetic */ boolean f$0;
    public final /* synthetic */ String f$1;
    public final /* synthetic */ BaseFragment f$2;

    public /* synthetic */ LoginActivity$$ExternalSyntheticLambda8(boolean z, String str, BaseFragment baseFragment) {
        this.f$0 = z;
        this.f$1 = str;
        this.f$2 = baseFragment;
    }

    public final void onClick(DialogInterface dialogInterface, int i) {
        LoginActivity.lambda$needShowInvalidAlert$10(this.f$0, this.f$1, this.f$2, dialogInterface, i);
    }
}
