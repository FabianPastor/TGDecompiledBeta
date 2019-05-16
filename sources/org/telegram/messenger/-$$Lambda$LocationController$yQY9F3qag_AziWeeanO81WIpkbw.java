package org.telegram.messenger;

import org.telegram.messenger.LocationController.SharingLocationInfo;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$LocationController$yQY9F3qag_AziWeeanO81WIpkbw implements Runnable {
    private final /* synthetic */ LocationController f$0;
    private final /* synthetic */ int f$1;
    private final /* synthetic */ SharingLocationInfo f$2;

    public /* synthetic */ -$$Lambda$LocationController$yQY9F3qag_AziWeeanO81WIpkbw(LocationController locationController, int i, SharingLocationInfo sharingLocationInfo) {
        this.f$0 = locationController;
        this.f$1 = i;
        this.f$2 = sharingLocationInfo;
    }

    public final void run() {
        this.f$0.lambda$saveSharingLocation$10$LocationController(this.f$1, this.f$2);
    }
}
