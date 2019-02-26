package org.telegram.ui;

import java.util.ArrayList;
import org.telegram.ui.PhotoViewer.AnonymousClass36;

final /* synthetic */ class PhotoViewer$36$$Lambda$0 implements Runnable {
    private final AnonymousClass36 arg$1;
    private final ArrayList arg$2;

    PhotoViewer$36$$Lambda$0(AnonymousClass36 anonymousClass36, ArrayList arrayList) {
        this.arg$1 = anonymousClass36;
        this.arg$2 = arrayList;
    }

    public void run() {
        this.arg$1.lambda$onPreDraw$0$PhotoViewer$36(this.arg$2);
    }
}
