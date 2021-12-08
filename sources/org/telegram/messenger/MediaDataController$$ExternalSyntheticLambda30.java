package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class MediaDataController$$ExternalSyntheticLambda30 implements RequestDelegate {
    public final /* synthetic */ MediaDataController f$0;
    public final /* synthetic */ long f$1;
    public final /* synthetic */ int f$2;
    public final /* synthetic */ int f$3;

    public /* synthetic */ MediaDataController$$ExternalSyntheticLambda30(MediaDataController mediaDataController, long j, int i, int i2) {
        this.f$0 = mediaDataController;
        this.f$1 = j;
        this.f$2 = i;
        this.f$3 = i2;
    }

    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
        this.f$0.m792x23406e51(this.f$1, this.f$2, this.f$3, tLObject, tL_error);
    }
}
