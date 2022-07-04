package org.telegram.messenger;

import android.content.DialogInterface;
import org.telegram.ui.ActionBar.BaseFragment;

public final /* synthetic */ class AndroidUtilities$$ExternalSyntheticLambda2 implements DialogInterface.OnClickListener {
    public final /* synthetic */ BaseFragment f$0;

    public /* synthetic */ AndroidUtilities$$ExternalSyntheticLambda2(BaseFragment baseFragment) {
        this.f$0 = baseFragment;
    }

    public final void onClick(DialogInterface dialogInterface, int i) {
        AndroidUtilities.lambda$isGoogleMapsInstalled$4(this.f$0, dialogInterface, i);
    }
}
