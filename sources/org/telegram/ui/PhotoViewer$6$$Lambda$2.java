package org.telegram.ui;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import org.telegram.ui.PhotoViewer.AnonymousClass6;

final /* synthetic */ class PhotoViewer$6$$Lambda$2 implements OnClickListener {
    private final AnonymousClass6 arg$1;
    private final boolean[] arg$2;

    PhotoViewer$6$$Lambda$2(AnonymousClass6 anonymousClass6, boolean[] zArr) {
        this.arg$1 = anonymousClass6;
        this.arg$2 = zArr;
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        this.arg$1.lambda$onItemClick$2$PhotoViewer$6(this.arg$2, dialogInterface, i);
    }
}
