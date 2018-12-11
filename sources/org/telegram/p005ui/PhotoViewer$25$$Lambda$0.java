package org.telegram.p005ui;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import org.telegram.p005ui.PhotoViewer.CLASSNAME;

/* renamed from: org.telegram.ui.PhotoViewer$25$$Lambda$0 */
final /* synthetic */ class PhotoViewer$25$$Lambda$0 implements OnClickListener {
    private final CLASSNAME arg$1;

    PhotoViewer$25$$Lambda$0(CLASSNAME CLASSNAME) {
        this.arg$1 = CLASSNAME;
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        this.arg$1.lambda$onError$0$PhotoViewer$25(dialogInterface, i);
    }
}
