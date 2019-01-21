package org.telegram.ui;

import org.telegram.ui.PhotoViewer.AnonymousClass35;
import org.telegram.ui.PhotoViewer.PlaceProviderObject;

final /* synthetic */ class PhotoViewer$35$$Lambda$2 implements Runnable {
    private final AnonymousClass35 arg$1;
    private final PlaceProviderObject arg$2;

    PhotoViewer$35$$Lambda$2(AnonymousClass35 anonymousClass35, PlaceProviderObject placeProviderObject) {
        this.arg$1 = anonymousClass35;
        this.arg$2 = placeProviderObject;
    }

    public void run() {
        this.arg$1.lambda$onPreDraw$2$PhotoViewer$35(this.arg$2);
    }
}
