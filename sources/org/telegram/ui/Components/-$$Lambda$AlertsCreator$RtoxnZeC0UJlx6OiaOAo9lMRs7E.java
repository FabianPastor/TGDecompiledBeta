package org.telegram.ui.Components;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$AlertsCreator$RtoxnZeC0UJlx6OiaOAo9lMRs7E implements OnClickListener {
    private final /* synthetic */ Runnable f$0;

    public /* synthetic */ -$$Lambda$AlertsCreator$RtoxnZeC0UJlx6OiaOAo9lMRs7E(Runnable runnable) {
        this.f$0 = runnable;
    }

    public final void onClick(DialogInterface dialogInterface, int i) {
        AlertsCreator.lambda$showSecretLocationAlert$4(this.f$0, dialogInterface, i);
    }
}
