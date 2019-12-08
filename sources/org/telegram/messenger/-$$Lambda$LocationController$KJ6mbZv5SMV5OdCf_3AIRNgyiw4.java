package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$LocationController$KJ6mbZv5SMV5OdCf_3AIRNgyiw4 implements RequestDelegate {
    private final /* synthetic */ LocationController f$0;
    private final /* synthetic */ long f$1;

    public /* synthetic */ -$$Lambda$LocationController$KJ6mbZv5SMV5OdCf_3AIRNgyiw4(LocationController locationController, long j) {
        this.f$0 = locationController;
        this.f$1 = j;
    }

    public final void run(TLObject tLObject, TL_error tL_error) {
        this.f$0.lambda$loadLiveLocations$24$LocationController(this.f$1, tLObject, tL_error);
    }
}
