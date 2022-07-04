package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class MediaDataController$$ExternalSyntheticLambda56 implements RequestDelegate {
    public final /* synthetic */ MediaDataController f$0;
    public final /* synthetic */ long f$1;
    public final /* synthetic */ int f$2;

    public /* synthetic */ MediaDataController$$ExternalSyntheticLambda56(MediaDataController mediaDataController, long j, int i) {
        this.f$0 = mediaDataController;
        this.f$1 = j;
        this.f$2 = i;
    }

    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
        this.f$0.m2098lambda$saveDraft$148$orgtelegrammessengerMediaDataController(this.f$1, this.f$2, tLObject, tL_error);
    }
}
