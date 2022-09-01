package org.telegram.ui;

import android.content.DialogInterface;

public final /* synthetic */ class PhotoViewer$$ExternalSyntheticLambda13 implements DialogInterface.OnDismissListener {
    public final /* synthetic */ Runnable f$0;

    public /* synthetic */ PhotoViewer$$ExternalSyntheticLambda13(Runnable runnable) {
        this.f$0 = runnable;
    }

    public final void onDismiss(DialogInterface dialogInterface) {
        this.f$0.run();
    }
}
