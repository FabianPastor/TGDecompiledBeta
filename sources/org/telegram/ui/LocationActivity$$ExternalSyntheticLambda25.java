package org.telegram.ui;

import org.telegram.messenger.LocationController;

public final /* synthetic */ class LocationActivity$$ExternalSyntheticLambda25 implements Runnable {
    public final /* synthetic */ LocationActivity f$0;
    public final /* synthetic */ LocationController.SharingLocationInfo f$1;

    public /* synthetic */ LocationActivity$$ExternalSyntheticLambda25(LocationActivity locationActivity, LocationController.SharingLocationInfo sharingLocationInfo) {
        this.f$0 = locationActivity;
        this.f$1 = sharingLocationInfo;
    }

    public final void run() {
        this.f$0.lambda$createView$5(this.f$1);
    }
}
