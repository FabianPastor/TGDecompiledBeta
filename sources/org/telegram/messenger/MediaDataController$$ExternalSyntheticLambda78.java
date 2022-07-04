package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class MediaDataController$$ExternalSyntheticLambda78 implements RequestDelegate {
    public final /* synthetic */ MediaDataController f$0;
    public final /* synthetic */ int[] f$1;
    public final /* synthetic */ long f$2;

    public /* synthetic */ MediaDataController$$ExternalSyntheticLambda78(MediaDataController mediaDataController, int[] iArr, long j) {
        this.f$0 = mediaDataController;
        this.f$1 = iArr;
        this.f$2 = j;
    }

    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
        this.f$0.m1995xb51c1a04(this.f$1, this.f$2, tLObject, tL_error);
    }
}
