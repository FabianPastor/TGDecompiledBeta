package org.telegram.messenger;

import org.telegram.messenger.LocationController.SharingLocationInfo;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$LocationController$PRv-6ObbwXmRgZBGVbIVGOjYQD8 implements Runnable {
    private final /* synthetic */ LocationController f$0;
    private final /* synthetic */ SharingLocationInfo f$1;

    public /* synthetic */ -$$Lambda$LocationController$PRv-6ObbwXmRgZBGVbIVGOjYQD8(LocationController locationController, SharingLocationInfo sharingLocationInfo) {
        this.f$0 = locationController;
        this.f$1 = sharingLocationInfo;
    }

    public final void run() {
        this.f$0.lambda$update$3$LocationController(this.f$1);
    }
}
