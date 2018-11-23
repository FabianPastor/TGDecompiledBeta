package org.telegram.p005ui;

import org.telegram.p005ui.PhotoViewer.C109336;
import org.telegram.p005ui.PhotoViewer.PlaceProviderObject;

/* renamed from: org.telegram.ui.PhotoViewer$36$$Lambda$2 */
final /* synthetic */ class PhotoViewer$36$$Lambda$2 implements Runnable {
    private final C109336 arg$1;
    private final PlaceProviderObject arg$2;

    PhotoViewer$36$$Lambda$2(C109336 c109336, PlaceProviderObject placeProviderObject) {
        this.arg$1 = c109336;
        this.arg$2 = placeProviderObject;
    }

    public void run() {
        this.arg$1.lambda$onPreDraw$2$PhotoViewer$36(this.arg$2);
    }
}
