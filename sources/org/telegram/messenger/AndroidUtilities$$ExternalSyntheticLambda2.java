package org.telegram.messenger;

import android.content.DialogInterface;
import org.telegram.ui.ActionBar.BaseFragment;

public final /* synthetic */ class AndroidUtilities$$ExternalSyntheticLambda2 implements DialogInterface.OnClickListener {
    public final /* synthetic */ String f$0;
    public final /* synthetic */ BaseFragment f$1;

    public /* synthetic */ AndroidUtilities$$ExternalSyntheticLambda2(String str, BaseFragment baseFragment) {
        this.f$0 = str;
        this.f$1 = baseFragment;
    }

    public final void onClick(DialogInterface dialogInterface, int i) {
        AndroidUtilities.lambda$isMapsInstalled$4(this.f$0, this.f$1, dialogInterface, i);
    }
}
