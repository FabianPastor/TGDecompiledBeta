package org.telegram.messenger;

import org.telegram.messenger.LocationController.SharingLocationInfo;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;

final /* synthetic */ class LocationController$$Lambda$1 implements RequestDelegate {
    private final LocationController arg$1;
    private final SharingLocationInfo arg$2;
    private final int[] arg$3;

    LocationController$$Lambda$1(LocationController locationController, SharingLocationInfo sharingLocationInfo, int[] iArr) {
        this.arg$1 = locationController;
        this.arg$2 = sharingLocationInfo;
        this.arg$3 = iArr;
    }

    public void run(TLObject tLObject, TL_error tL_error) {
        this.arg$1.lambda$broadcastLastKnownLocation$2$LocationController(this.arg$2, this.arg$3, tLObject, tL_error);
    }
}
