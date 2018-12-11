package org.telegram.p005ui;

import java.util.ArrayList;
import org.telegram.p005ui.PhotoViewer.CLASSNAME;

/* renamed from: org.telegram.ui.PhotoViewer$35$$Lambda$0 */
final /* synthetic */ class PhotoViewer$35$$Lambda$0 implements Runnable {
    private final CLASSNAME arg$1;
    private final ArrayList arg$2;

    PhotoViewer$35$$Lambda$0(CLASSNAME CLASSNAME, ArrayList arrayList) {
        this.arg$1 = CLASSNAME;
        this.arg$2 = arrayList;
    }

    public void run() {
        this.arg$1.lambda$onPreDraw$0$PhotoViewer$35(this.arg$2);
    }
}
