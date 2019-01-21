package org.telegram.ui;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import org.telegram.ui.PhotoViewer.AnonymousClass25;

final /* synthetic */ class PhotoViewer$25$$Lambda$0 implements OnClickListener {
    private final AnonymousClass25 arg$1;

    PhotoViewer$25$$Lambda$0(AnonymousClass25 anonymousClass25) {
        this.arg$1 = anonymousClass25;
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        this.arg$1.lambda$onError$0$PhotoViewer$25(dialogInterface, i);
    }
}
