package org.telegram.ui;

import android.content.DialogInterface;

public final /* synthetic */ class PhotoViewer$$ExternalSyntheticLambda1 implements DialogInterface.OnDismissListener {
    public final /* synthetic */ Runnable f$0;

    public /* synthetic */ PhotoViewer$$ExternalSyntheticLambda1(Runnable runnable) {
        this.f$0 = runnable;
    }

    public final void onDismiss(DialogInterface dialogInterface) {
        this.f$0.run();
    }
}
