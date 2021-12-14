package org.telegram.messenger;

import org.telegram.messenger.LocationController;

public final /* synthetic */ class LocationController$$ExternalSyntheticLambda19 implements Runnable {
    public final /* synthetic */ LocationController f$0;
    public final /* synthetic */ LocationController.SharingLocationInfo f$1;

    public /* synthetic */ LocationController$$ExternalSyntheticLambda19(LocationController locationController, LocationController.SharingLocationInfo sharingLocationInfo) {
        this.f$0 = locationController;
        this.f$1 = sharingLocationInfo;
    }

    public final void run() {
        this.f$0.lambda$broadcastLastKnownLocation$6(this.f$1);
    }
}
