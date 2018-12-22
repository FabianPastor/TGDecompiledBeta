package org.telegram.messenger;

import org.telegram.messenger.LocationController.SharingLocationInfo;

final /* synthetic */ class LocationController$$Lambda$2 implements Runnable {
    private final LocationController arg$1;
    private final SharingLocationInfo arg$2;

    LocationController$$Lambda$2(LocationController locationController, SharingLocationInfo sharingLocationInfo) {
        this.arg$1 = locationController;
        this.arg$2 = sharingLocationInfo;
    }

    public void run() {
        this.arg$1.lambda$update$3$LocationController(this.arg$2);
    }
}
