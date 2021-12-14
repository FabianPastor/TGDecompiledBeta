package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

public final /* synthetic */ class LocationController$$ExternalSyntheticLambda26 implements RequestDelegate {
    public final /* synthetic */ LocationController f$0;

    public /* synthetic */ LocationController$$ExternalSyntheticLambda26(LocationController locationController) {
        this.f$0 = locationController;
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        this.f$0.lambda$removeAllLocationSharings$23(tLObject, tLRPC$TL_error);
    }
}
