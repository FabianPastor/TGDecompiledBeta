package org.telegram.p005ui;

import org.telegram.p005ui.PhotoViewer.CLASSNAME;
import org.telegram.p005ui.PhotoViewer.PlaceProviderObject;

/* renamed from: org.telegram.ui.PhotoViewer$35$$Lambda$2 */
final /* synthetic */ class PhotoViewer$35$$Lambda$2 implements Runnable {
    private final CLASSNAME arg$1;
    private final PlaceProviderObject arg$2;

    PhotoViewer$35$$Lambda$2(CLASSNAME CLASSNAME, PlaceProviderObject placeProviderObject) {
        this.arg$1 = CLASSNAME;
        this.arg$2 = placeProviderObject;
    }

    public void run() {
        this.arg$1.lambda$onPreDraw$2$PhotoViewer$35(this.arg$2);
    }
}
