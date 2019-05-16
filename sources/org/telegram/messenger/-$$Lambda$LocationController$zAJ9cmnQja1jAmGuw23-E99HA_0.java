package org.telegram.messenger;

import org.telegram.messenger.LocationController.SharingLocationInfo;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$LocationController$zAJ9cmnQja1jAmGuw23-E99HA_0 implements Runnable {
    private final /* synthetic */ LocationController f$0;
    private final /* synthetic */ SharingLocationInfo f$1;
    private final /* synthetic */ SharingLocationInfo f$2;

    public /* synthetic */ -$$Lambda$LocationController$zAJ9cmnQja1jAmGuw23-E99HA_0(LocationController locationController, SharingLocationInfo sharingLocationInfo, SharingLocationInfo sharingLocationInfo2) {
        this.f$0 = locationController;
        this.f$1 = sharingLocationInfo;
        this.f$2 = sharingLocationInfo2;
    }

    public final void run() {
        this.f$0.lambda$addSharingLocation$5$LocationController(this.f$1, this.f$2);
    }
}
