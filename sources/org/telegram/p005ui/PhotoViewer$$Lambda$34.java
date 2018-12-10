package org.telegram.p005ui;

import org.telegram.p005ui.PhotoViewer.PlaceProviderObject;

/* renamed from: org.telegram.ui.PhotoViewer$$Lambda$34 */
final /* synthetic */ class PhotoViewer$$Lambda$34 implements Runnable {
    private final PhotoViewer arg$1;
    private final PlaceProviderObject arg$2;

    PhotoViewer$$Lambda$34(PhotoViewer photoViewer, PlaceProviderObject placeProviderObject) {
        this.arg$1 = photoViewer;
        this.arg$2 = placeProviderObject;
    }

    public void run() {
        this.arg$1.lambda$closePhoto$42$PhotoViewer(this.arg$2);
    }
}
