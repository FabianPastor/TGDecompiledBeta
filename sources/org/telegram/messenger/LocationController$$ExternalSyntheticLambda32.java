package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;
/* loaded from: classes.dex */
public final /* synthetic */ class LocationController$$ExternalSyntheticLambda32 implements RequestDelegate {
    public static final /* synthetic */ LocationController$$ExternalSyntheticLambda32 INSTANCE = new LocationController$$ExternalSyntheticLambda32();

    private /* synthetic */ LocationController$$ExternalSyntheticLambda32() {
    }

    @Override // org.telegram.tgnet.RequestDelegate
    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        LocationController.lambda$broadcastLastKnownLocation$8(tLObject, tLRPC$TL_error);
    }
}
