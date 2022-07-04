package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class MediaDataController$$ExternalSyntheticLambda51 implements RequestDelegate {
    public final /* synthetic */ MediaDataController f$0;
    public final /* synthetic */ int f$1;
    public final /* synthetic */ String f$2;
    public final /* synthetic */ String f$3;

    public /* synthetic */ MediaDataController$$ExternalSyntheticLambda51(MediaDataController mediaDataController, int i, String str, String str2) {
        this.f$0 = mediaDataController;
        this.f$1 = i;
        this.f$2 = str;
        this.f$3 = str2;
    }

    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
        this.f$0.m1987x9520f5f6(this.f$1, this.f$2, this.f$3, tLObject, tL_error);
    }
}
