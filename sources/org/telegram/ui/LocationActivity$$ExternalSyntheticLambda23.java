package org.telegram.ui;

import org.telegram.messenger.IMapsProvider;

public final /* synthetic */ class LocationActivity$$ExternalSyntheticLambda23 implements Runnable {
    public final /* synthetic */ LocationActivity f$0;
    public final /* synthetic */ IMapsProvider.IMapView f$1;

    public /* synthetic */ LocationActivity$$ExternalSyntheticLambda23(LocationActivity locationActivity, IMapsProvider.IMapView iMapView) {
        this.f$0 = locationActivity;
        this.f$1 = iMapView;
    }

    public final void run() {
        this.f$0.lambda$createView$21(this.f$1);
    }
}
