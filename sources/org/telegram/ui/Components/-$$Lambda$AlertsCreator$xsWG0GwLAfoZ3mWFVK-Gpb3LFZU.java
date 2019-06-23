package org.telegram.ui.Components;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$AlertsCreator$xsWG0GwLAfoZ3mWFVK-Gpb3LFZU implements OnClickListener {
    private final /* synthetic */ Runnable f$0;

    public /* synthetic */ -$$Lambda$AlertsCreator$xsWG0GwLAfoZ3mWFVK-Gpb3LFZU(Runnable runnable) {
        this.f$0 = runnable;
    }

    public final void onClick(DialogInterface dialogInterface, int i) {
        AlertsCreator.lambda$showSecretLocationAlert$6(this.f$0, dialogInterface, i);
    }
}
