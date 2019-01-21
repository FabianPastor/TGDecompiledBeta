package org.telegram.ui;

import java.util.ArrayList;
import org.telegram.ui.PhotoViewer.AnonymousClass35;

final /* synthetic */ class PhotoViewer$35$$Lambda$0 implements Runnable {
    private final AnonymousClass35 arg$1;
    private final ArrayList arg$2;

    PhotoViewer$35$$Lambda$0(AnonymousClass35 anonymousClass35, ArrayList arrayList) {
        this.arg$1 = anonymousClass35;
        this.arg$2 = arrayList;
    }

    public void run() {
        this.arg$1.lambda$onPreDraw$0$PhotoViewer$35(this.arg$2);
    }
}
