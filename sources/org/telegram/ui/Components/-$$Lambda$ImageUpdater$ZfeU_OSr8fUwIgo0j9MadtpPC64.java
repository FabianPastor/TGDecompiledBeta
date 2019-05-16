package org.telegram.ui.Components;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$ImageUpdater$ZfeU_OSr8fUwIgo0j9MadtpPCLASSNAME implements OnClickListener {
    private final /* synthetic */ ImageUpdater f$0;
    private final /* synthetic */ Runnable f$1;

    public /* synthetic */ -$$Lambda$ImageUpdater$ZfeU_OSr8fUwIgo0j9MadtpPCLASSNAME(ImageUpdater imageUpdater, Runnable runnable) {
        this.f$0 = imageUpdater;
        this.f$1 = runnable;
    }

    public final void onClick(DialogInterface dialogInterface, int i) {
        this.f$0.lambda$openMenu$0$ImageUpdater(this.f$1, dialogInterface, i);
    }
}
