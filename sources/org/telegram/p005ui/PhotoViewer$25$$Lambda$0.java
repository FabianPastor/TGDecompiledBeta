package org.telegram.p005ui;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import org.telegram.p005ui.PhotoViewer.C153625;

/* renamed from: org.telegram.ui.PhotoViewer$25$$Lambda$0 */
final /* synthetic */ class PhotoViewer$25$$Lambda$0 implements OnClickListener {
    private final C153625 arg$1;

    PhotoViewer$25$$Lambda$0(C153625 c153625) {
        this.arg$1 = c153625;
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        this.arg$1.lambda$onError$0$PhotoViewer$25(dialogInterface, i);
    }
}
