package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$LocationActivity$10_gJYtZI1WBT9zW2NISAwbjMSQ implements RequestDelegate {
    private final /* synthetic */ LocationActivity f$0;
    private final /* synthetic */ long f$1;

    public /* synthetic */ -$$Lambda$LocationActivity$10_gJYtZI1WBT9zW2NISAwbjMSQ(LocationActivity locationActivity, long j) {
        this.f$0 = locationActivity;
        this.f$1 = j;
    }

    public final void run(TLObject tLObject, TL_error tL_error) {
        this.f$0.lambda$getRecentLocations$20$LocationActivity(this.f$1, tLObject, tL_error);
    }
}