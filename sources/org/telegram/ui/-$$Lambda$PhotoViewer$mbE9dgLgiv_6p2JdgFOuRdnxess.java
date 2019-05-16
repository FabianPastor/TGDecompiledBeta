package org.telegram.ui;

import org.telegram.ui.PhotoViewer.PlaceProviderObject;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$PhotoViewer$mbE9dgLgiv_6p2JdgFOuRdnxess implements Runnable {
    private final /* synthetic */ PhotoViewer f$0;
    private final /* synthetic */ PlaceProviderObject f$1;

    public /* synthetic */ -$$Lambda$PhotoViewer$mbE9dgLgiv_6p2JdgFOuRdnxess(PhotoViewer photoViewer, PlaceProviderObject placeProviderObject) {
        this.f$0 = photoViewer;
        this.f$1 = placeProviderObject;
    }

    public final void run() {
        this.f$0.lambda$closePhoto$43$PhotoViewer(this.f$1);
    }
}
