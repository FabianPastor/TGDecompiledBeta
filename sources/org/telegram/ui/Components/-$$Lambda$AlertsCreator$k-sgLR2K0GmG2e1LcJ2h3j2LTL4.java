package org.telegram.ui.Components;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import org.telegram.messenger.MessagesController;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$AlertsCreator$k-sgLR2K0GmG2e1LcJ2h3j2LTL4 implements OnClickListener {
    private final /* synthetic */ int[] f$0;

    public /* synthetic */ -$$Lambda$AlertsCreator$k-sgLR2K0GmG2e1LcJ2h3j2LTL4(int[] iArr) {
        this.f$0 = iArr;
    }

    public final void onClick(DialogInterface dialogInterface, int i) {
        MessagesController.getGlobalMainSettings().edit().putInt("keep_media", this.f$0[0]).commit();
    }
}
