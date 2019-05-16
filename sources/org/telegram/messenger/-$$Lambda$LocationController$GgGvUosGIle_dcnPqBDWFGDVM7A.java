package org.telegram.messenger;

import org.telegram.messenger.LocationController.SharingLocationInfo;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$LocationController$GgGvUosGIle_dcnPqBDWFGDVM7A implements RequestDelegate {
    private final /* synthetic */ LocationController f$0;
    private final /* synthetic */ SharingLocationInfo f$1;
    private final /* synthetic */ int[] f$2;

    public /* synthetic */ -$$Lambda$LocationController$GgGvUosGIle_dcnPqBDWFGDVM7A(LocationController locationController, SharingLocationInfo sharingLocationInfo, int[] iArr) {
        this.f$0 = locationController;
        this.f$1 = sharingLocationInfo;
        this.f$2 = iArr;
    }

    public final void run(TLObject tLObject, TL_error tL_error) {
        this.f$0.lambda$broadcastLastKnownLocation$2$LocationController(this.f$1, this.f$2, tLObject, tL_error);
    }
}
