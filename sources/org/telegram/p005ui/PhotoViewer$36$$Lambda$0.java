package org.telegram.p005ui;

import java.util.ArrayList;
import org.telegram.p005ui.PhotoViewer.C109336;

/* renamed from: org.telegram.ui.PhotoViewer$36$$Lambda$0 */
final /* synthetic */ class PhotoViewer$36$$Lambda$0 implements Runnable {
    private final C109336 arg$1;
    private final ArrayList arg$2;

    PhotoViewer$36$$Lambda$0(C109336 c109336, ArrayList arrayList) {
        this.arg$1 = c109336;
        this.arg$2 = arrayList;
    }

    public void run() {
        this.arg$1.lambda$onPreDraw$0$PhotoViewer$36(this.arg$2);
    }
}
