package org.telegram.ui;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;

final /* synthetic */ class PhotoViewer$$Lambda$23 implements OnClickListener {
    private final PhotoViewer arg$1;

    PhotoViewer$$Lambda$23(PhotoViewer photoViewer) {
        this.arg$1 = photoViewer;
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        this.arg$1.lambda$checkInlinePermissions$29$PhotoViewer(dialogInterface, i);
    }
}
