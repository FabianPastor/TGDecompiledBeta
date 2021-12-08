package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class MediaDataController$$ExternalSyntheticLambda40 implements RequestDelegate {
    public final /* synthetic */ MediaDataController f$0;
    public final /* synthetic */ String f$1;

    public /* synthetic */ MediaDataController$$ExternalSyntheticLambda40(MediaDataController mediaDataController, String str) {
        this.f$0 = mediaDataController;
        this.f$1 = str;
    }

    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
        this.f$0.m906xfvar_b(this.f$1, tLObject, tL_error);
    }
}
