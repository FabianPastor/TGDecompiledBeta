package org.telegram.ui;

import org.telegram.ui.PhotoViewer;

public final /* synthetic */ class SecretMediaViewer$$ExternalSyntheticLambda5 implements Runnable {
    public final /* synthetic */ SecretMediaViewer f$0;
    public final /* synthetic */ PhotoViewer.PlaceProviderObject f$1;

    public /* synthetic */ SecretMediaViewer$$ExternalSyntheticLambda5(SecretMediaViewer secretMediaViewer, PhotoViewer.PlaceProviderObject placeProviderObject) {
        this.f$0 = secretMediaViewer;
        this.f$1 = placeProviderObject;
    }

    public final void run() {
        this.f$0.lambda$closePhoto$3(this.f$1);
    }
}
