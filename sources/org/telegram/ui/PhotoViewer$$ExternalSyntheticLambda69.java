package org.telegram.ui;

import org.telegram.ui.PhotoViewer;

public final /* synthetic */ class PhotoViewer$$ExternalSyntheticLambda69 implements Runnable {
    public final /* synthetic */ PhotoViewer f$0;
    public final /* synthetic */ PhotoViewer.PlaceProviderObject f$1;

    public /* synthetic */ PhotoViewer$$ExternalSyntheticLambda69(PhotoViewer photoViewer, PhotoViewer.PlaceProviderObject placeProviderObject) {
        this.f$0 = photoViewer;
        this.f$1 = placeProviderObject;
    }

    public final void run() {
        this.f$0.lambda$onPhotoClosed$77(this.f$1);
    }
}
