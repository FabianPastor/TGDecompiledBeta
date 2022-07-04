package org.telegram.messenger;

import org.telegram.messenger.LocationController;

public final /* synthetic */ class LocationController$$ExternalSyntheticLambda22 implements Runnable {
    public final /* synthetic */ LocationController f$0;
    public final /* synthetic */ LocationController.SharingLocationInfo f$1;
    public final /* synthetic */ LocationController.SharingLocationInfo f$2;

    public /* synthetic */ LocationController$$ExternalSyntheticLambda22(LocationController locationController, LocationController.SharingLocationInfo sharingLocationInfo, LocationController.SharingLocationInfo sharingLocationInfo2) {
        this.f$0 = locationController;
        this.f$1 = sharingLocationInfo;
        this.f$2 = sharingLocationInfo2;
    }

    public final void run() {
        this.f$0.lambda$addSharingLocation$12(this.f$1, this.f$2);
    }
}
