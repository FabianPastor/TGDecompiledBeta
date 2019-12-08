package org.telegram.messenger;

import org.telegram.messenger.LocationController.SharingLocationInfo;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$LocationController$J9E_uuS3DUnAFkelpP5g5PQKIYU implements Runnable {
    private final /* synthetic */ LocationController f$0;
    private final /* synthetic */ int f$1;
    private final /* synthetic */ SharingLocationInfo f$2;

    public /* synthetic */ -$$Lambda$LocationController$J9E_uuS3DUnAFkelpP5g5PQKIYU(LocationController locationController, int i, SharingLocationInfo sharingLocationInfo) {
        this.f$0 = locationController;
        this.f$1 = i;
        this.f$2 = sharingLocationInfo;
    }

    public final void run() {
        this.f$0.lambda$saveSharingLocation$15$LocationController(this.f$1, this.f$2);
    }
}
