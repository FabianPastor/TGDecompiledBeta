package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$LocationActivity$mUtxBXce22gm9LBjol2U5VYR5cg implements RequestDelegate {
    private final /* synthetic */ LocationActivity f$0;
    private final /* synthetic */ long f$1;

    public /* synthetic */ -$$Lambda$LocationActivity$mUtxBXce22gm9LBjol2U5VYR5cg(LocationActivity locationActivity, long j) {
        this.f$0 = locationActivity;
        this.f$1 = j;
    }

    public final void run(TLObject tLObject, TL_error tL_error) {
        this.f$0.lambda$getRecentLocations$26$LocationActivity(this.f$1, tLObject, tL_error);
    }
}
