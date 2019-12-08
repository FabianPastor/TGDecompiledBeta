package org.telegram.messenger;

import org.telegram.messenger.LocationController.SharingLocationInfo;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$LocationController$Pu-U11hvPEurl1Ab6Bm2sYLQ1rg implements Runnable {
    private final /* synthetic */ LocationController f$0;
    private final /* synthetic */ SharingLocationInfo f$1;
    private final /* synthetic */ SharingLocationInfo f$2;

    public /* synthetic */ -$$Lambda$LocationController$Pu-U11hvPEurl1Ab6Bm2sYLQ1rg(LocationController locationController, SharingLocationInfo sharingLocationInfo, SharingLocationInfo sharingLocationInfo2) {
        this.f$0 = locationController;
        this.f$1 = sharingLocationInfo;
        this.f$2 = sharingLocationInfo2;
    }

    public final void run() {
        this.f$0.lambda$addSharingLocation$10$LocationController(this.f$1, this.f$2);
    }
}
