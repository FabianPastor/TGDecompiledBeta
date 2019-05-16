package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$DataQuery$TygWBWHmoEKh7kTWN3Sp59N9f8M implements RequestDelegate {
    public static final /* synthetic */ -$$Lambda$DataQuery$TygWBWHmoEKh7kTWN3Sp59N9f8M INSTANCE = new -$$Lambda$DataQuery$TygWBWHmoEKh7kTWN3Sp59N9f8M();

    private /* synthetic */ -$$Lambda$DataQuery$TygWBWHmoEKh7kTWN3Sp59N9f8M() {
    }

    public final void run(TLObject tLObject, TL_error tL_error) {
        DataQuery.lambda$markFaturedStickersAsRead$26(tLObject, tL_error);
    }
}
