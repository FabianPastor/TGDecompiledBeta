package org.telegram.ui;

import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;

final /* synthetic */ class PhotoViewer$$Lambda$27 implements OnDismissListener {
    private final PhotoViewer arg$1;

    PhotoViewer$$Lambda$27(PhotoViewer photoViewer) {
        this.arg$1 = photoViewer;
    }

    public void onDismiss(DialogInterface dialogInterface) {
        this.arg$1.lambda$showAlertDialog$33$PhotoViewer(dialogInterface);
    }
}
