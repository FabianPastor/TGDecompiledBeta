package org.telegram.messenger;

import org.telegram.messenger.LocationController.SharingLocationInfo;

final /* synthetic */ class LocationController$$Lambda$6 implements Runnable {
    private final LocationController arg$1;
    private final int arg$2;
    private final SharingLocationInfo arg$3;

    LocationController$$Lambda$6(LocationController locationController, int i, SharingLocationInfo sharingLocationInfo) {
        this.arg$1 = locationController;
        this.arg$2 = i;
        this.arg$3 = sharingLocationInfo;
    }

    public void run() {
        this.arg$1.lambda$saveSharingLocation$10$LocationController(this.arg$2, this.arg$3);
    }
}
