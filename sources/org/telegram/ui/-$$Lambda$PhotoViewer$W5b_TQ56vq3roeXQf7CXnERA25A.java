package org.telegram.ui;

import org.telegram.ui.PhotoViewer.PlaceProviderObject;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$PhotoViewer$W5b_TQ56vq3roeXQf7CXnERA25A implements Runnable {
    private final /* synthetic */ PhotoViewer f$0;
    private final /* synthetic */ PlaceProviderObject f$1;

    public /* synthetic */ -$$Lambda$PhotoViewer$W5b_TQ56vq3roeXQf7CXnERA25A(PhotoViewer photoViewer, PlaceProviderObject placeProviderObject) {
        this.f$0 = photoViewer;
        this.f$1 = placeProviderObject;
    }

    public final void run() {
        this.f$0.lambda$onPhotoClosed$47$PhotoViewer(this.f$1);
    }
}
