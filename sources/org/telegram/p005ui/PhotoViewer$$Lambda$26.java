package org.telegram.p005ui;

import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;

/* renamed from: org.telegram.ui.PhotoViewer$$Lambda$26 */
final /* synthetic */ class PhotoViewer$$Lambda$26 implements OnDismissListener {
    private final PhotoViewer arg$1;

    PhotoViewer$$Lambda$26(PhotoViewer photoViewer) {
        this.arg$1 = photoViewer;
    }

    public void onDismiss(DialogInterface dialogInterface) {
        this.arg$1.lambda$showAlertDialog$32$PhotoViewer(dialogInterface);
    }
}
