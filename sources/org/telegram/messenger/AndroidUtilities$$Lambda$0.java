package org.telegram.messenger;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import org.telegram.p005ui.ActionBar.BaseFragment;

final /* synthetic */ class AndroidUtilities$$Lambda$0 implements OnClickListener {
    private final BaseFragment arg$1;

    AndroidUtilities$$Lambda$0(BaseFragment baseFragment) {
        this.arg$1 = baseFragment;
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        AndroidUtilities.lambda$isGoogleMapsInstalled$0$AndroidUtilities(this.arg$1, dialogInterface, i);
    }
}
