package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$LocationActivity$D3WKfJWdqIkwzPGXO10yP5dl6eE implements RequestDelegate {
    private final /* synthetic */ LocationActivity f$0;
    private final /* synthetic */ long f$1;

    public /* synthetic */ -$$Lambda$LocationActivity$D3WKfJWdqIkwzPGXO10yP5dl6eE(LocationActivity locationActivity, long j) {
        this.f$0 = locationActivity;
        this.f$1 = j;
    }

    public final void run(TLObject tLObject, TL_error tL_error) {
        this.f$0.lambda$getRecentLocations$17$LocationActivity(this.f$1, tLObject, tL_error);
    }
}
