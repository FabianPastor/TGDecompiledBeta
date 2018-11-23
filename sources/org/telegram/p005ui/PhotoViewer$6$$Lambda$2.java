package org.telegram.p005ui;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import org.telegram.p005ui.PhotoViewer.C15396;

/* renamed from: org.telegram.ui.PhotoViewer$6$$Lambda$2 */
final /* synthetic */ class PhotoViewer$6$$Lambda$2 implements OnClickListener {
    private final C15396 arg$1;
    private final boolean[] arg$2;

    PhotoViewer$6$$Lambda$2(C15396 c15396, boolean[] zArr) {
        this.arg$1 = c15396;
        this.arg$2 = zArr;
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        this.arg$1.lambda$onItemClick$2$PhotoViewer$6(this.arg$2, dialogInterface, i);
    }
}
