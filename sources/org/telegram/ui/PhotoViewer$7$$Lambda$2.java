package org.telegram.ui;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import org.telegram.ui.PhotoViewer.AnonymousClass7;

final /* synthetic */ class PhotoViewer$7$$Lambda$2 implements OnClickListener {
    private final AnonymousClass7 arg$1;
    private final boolean[] arg$2;

    PhotoViewer$7$$Lambda$2(AnonymousClass7 anonymousClass7, boolean[] zArr) {
        this.arg$1 = anonymousClass7;
        this.arg$2 = zArr;
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        this.arg$1.lambda$onItemClick$2$PhotoViewer$7(this.arg$2, dialogInterface, i);
    }
}
