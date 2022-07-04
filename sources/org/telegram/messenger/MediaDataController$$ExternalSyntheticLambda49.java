package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class MediaDataController$$ExternalSyntheticLambda49 implements RequestDelegate {
    public final /* synthetic */ MediaDataController f$0;
    public final /* synthetic */ int f$1;

    public /* synthetic */ MediaDataController$$ExternalSyntheticLambda49(MediaDataController mediaDataController, int i) {
        this.f$0 = mediaDataController;
        this.f$1 = i;
    }

    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
        this.f$0.m2049xaa6e4d45(this.f$1, tLObject, tL_error);
    }
}
