package org.telegram.ui;

import org.telegram.ui.PhotoViewer;

public final /* synthetic */ class PhotoViewer$65$$ExternalSyntheticLambda1 implements Runnable {
    public final /* synthetic */ PhotoViewer.AnonymousClass65 f$0;
    public final /* synthetic */ PhotoViewer.PlaceProviderObject f$1;

    public /* synthetic */ PhotoViewer$65$$ExternalSyntheticLambda1(PhotoViewer.AnonymousClass65 r1, PhotoViewer.PlaceProviderObject placeProviderObject) {
        this.f$0 = r1;
        this.f$1 = placeProviderObject;
    }

    public final void run() {
        this.f$0.lambda$onPreDraw$2(this.f$1);
    }
}
