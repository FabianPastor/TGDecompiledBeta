package org.telegram.ui.Components;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import org.telegram.messenger.SharedConfig;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$AlertsCreator$KHkLeEyAM3ueqVXJldazmtZAKQY implements OnClickListener {
    private final /* synthetic */ int[] f$0;

    public /* synthetic */ -$$Lambda$AlertsCreator$KHkLeEyAM3ueqVXJldazmtZAKQY(int[] iArr) {
        this.f$0 = iArr;
    }

    public final void onClick(DialogInterface dialogInterface, int i) {
        SharedConfig.setKeepMedia(this.f$0[0]);
    }
}
