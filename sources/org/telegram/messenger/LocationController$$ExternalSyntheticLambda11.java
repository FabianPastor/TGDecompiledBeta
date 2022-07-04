package org.telegram.messenger;

import org.telegram.messenger.LocationController;

public final /* synthetic */ class LocationController$$ExternalSyntheticLambda11 implements Runnable {
    public final /* synthetic */ LocationController f$0;
    public final /* synthetic */ int f$1;
    public final /* synthetic */ LocationController.SharingLocationInfo f$2;

    public /* synthetic */ LocationController$$ExternalSyntheticLambda11(LocationController locationController, int i, LocationController.SharingLocationInfo sharingLocationInfo) {
        this.f$0 = locationController;
        this.f$1 = i;
        this.f$2 = sharingLocationInfo;
    }

    public final void run() {
        this.f$0.lambda$saveSharingLocation$19(this.f$1, this.f$2);
    }
}
