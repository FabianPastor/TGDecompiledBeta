package org.telegram.ui;

import org.telegram.ui.PhotoViewer.AnonymousClass36;
import org.telegram.ui.PhotoViewer.PlaceProviderObject;

final /* synthetic */ class PhotoViewer$36$$Lambda$2 implements Runnable {
    private final AnonymousClass36 arg$1;
    private final PlaceProviderObject arg$2;

    PhotoViewer$36$$Lambda$2(AnonymousClass36 anonymousClass36, PlaceProviderObject placeProviderObject) {
        this.arg$1 = anonymousClass36;
        this.arg$2 = placeProviderObject;
    }

    public void run() {
        this.arg$1.lambda$onPreDraw$2$PhotoViewer$36(this.arg$2);
    }
}
