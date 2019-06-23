package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$MediaDataController$xhEg66KcWaHaHYMDQHDEwrYhFQY implements RequestDelegate {
    private final /* synthetic */ MediaDataController f$0;
    private final /* synthetic */ long f$1;
    private final /* synthetic */ int f$2;
    private final /* synthetic */ int f$3;

    public /* synthetic */ -$$Lambda$MediaDataController$xhEg66KcWaHaHYMDQHDEwrYhFQY(MediaDataController mediaDataController, long j, int i, int i2) {
        this.f$0 = mediaDataController;
        this.f$1 = j;
        this.f$2 = i;
        this.f$3 = i2;
    }

    public final void run(TLObject tLObject, TL_error tL_error) {
        this.f$0.lambda$getMediaCount$58$MediaDataController(this.f$1, this.f$2, this.f$3, tLObject, tL_error);
    }
}
