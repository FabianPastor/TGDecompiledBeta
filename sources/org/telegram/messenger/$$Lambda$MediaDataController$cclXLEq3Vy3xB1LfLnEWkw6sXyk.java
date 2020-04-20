package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

/* renamed from: org.telegram.messenger.-$$Lambda$MediaDataController$cclXLEq3Vy3xB1LfLnEWkw6sXyk  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$MediaDataController$cclXLEq3Vy3xB1LfLnEWkw6sXyk implements RequestDelegate {
    public static final /* synthetic */ $$Lambda$MediaDataController$cclXLEq3Vy3xB1LfLnEWkw6sXyk INSTANCE = new $$Lambda$MediaDataController$cclXLEq3Vy3xB1LfLnEWkw6sXyk();

    private /* synthetic */ $$Lambda$MediaDataController$cclXLEq3Vy3xB1LfLnEWkw6sXyk() {
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        MediaDataController.lambda$removePeer$95(tLObject, tLRPC$TL_error);
    }
}
