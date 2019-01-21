package org.telegram.ui;

import org.telegram.ui.PhotoViewer.PlaceProviderObject;

final /* synthetic */ class SecretMediaViewer$$Lambda$3 implements Runnable {
    private final SecretMediaViewer arg$1;
    private final PlaceProviderObject arg$2;

    SecretMediaViewer$$Lambda$3(SecretMediaViewer secretMediaViewer, PlaceProviderObject placeProviderObject) {
        this.arg$1 = secretMediaViewer;
        this.arg$2 = placeProviderObject;
    }

    public void run() {
        this.arg$1.lambda$closePhoto$3$SecretMediaViewer(this.arg$2);
    }
}
