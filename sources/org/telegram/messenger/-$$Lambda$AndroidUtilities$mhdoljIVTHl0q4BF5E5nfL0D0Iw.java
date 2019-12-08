package org.telegram.messenger;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import org.telegram.ui.ActionBar.BaseFragment;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$AndroidUtilities$mhdoljIVTHl0q4BF5E5nfL0D0Iw implements OnClickListener {
    private final /* synthetic */ BaseFragment f$0;

    public /* synthetic */ -$$Lambda$AndroidUtilities$mhdoljIVTHl0q4BF5E5nfL0D0Iw(BaseFragment baseFragment) {
        this.f$0 = baseFragment;
    }

    public final void onClick(DialogInterface dialogInterface, int i) {
        AndroidUtilities.lambda$isGoogleMapsInstalled$2(this.f$0, dialogInterface, i);
    }
}
