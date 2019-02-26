package org.telegram.ui;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import org.telegram.ui.PhotoViewer.AnonymousClass26;

final /* synthetic */ class PhotoViewer$26$$Lambda$0 implements OnClickListener {
    private final AnonymousClass26 arg$1;

    PhotoViewer$26$$Lambda$0(AnonymousClass26 anonymousClass26) {
        this.arg$1 = anonymousClass26;
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        this.arg$1.lambda$onError$0$PhotoViewer$26(dialogInterface, i);
    }
}
