package org.telegram.p005ui;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;

/* renamed from: org.telegram.ui.PhotoViewer$$Lambda$22 */
final /* synthetic */ class PhotoViewer$$Lambda$22 implements OnClickListener {
    private final PhotoViewer arg$1;

    PhotoViewer$$Lambda$22(PhotoViewer photoViewer) {
        this.arg$1 = photoViewer;
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        this.arg$1.lambda$checkInlinePermissions$28$PhotoViewer(dialogInterface, i);
    }
}
