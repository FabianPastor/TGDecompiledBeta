package org.telegram.messenger;

import org.telegram.messenger.LocationController.SharingLocationInfo;

final /* synthetic */ class LocationController$$Lambda$4 implements Runnable {
    private final LocationController arg$1;
    private final SharingLocationInfo arg$2;
    private final SharingLocationInfo arg$3;

    LocationController$$Lambda$4(LocationController locationController, SharingLocationInfo sharingLocationInfo, SharingLocationInfo sharingLocationInfo2) {
        this.arg$1 = locationController;
        this.arg$2 = sharingLocationInfo;
        this.arg$3 = sharingLocationInfo2;
    }

    public void run() {
        this.arg$1.lambda$addSharingLocation$5$LocationController(this.arg$2, this.arg$3);
    }
}
