package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class MediaDataController$$ExternalSyntheticLambda42 implements RequestDelegate {
    public final /* synthetic */ MediaDataController f$0;
    public final /* synthetic */ String f$1;
    public final /* synthetic */ boolean f$2;

    public /* synthetic */ MediaDataController$$ExternalSyntheticLambda42(MediaDataController mediaDataController, String str, boolean z) {
        this.f$0 = mediaDataController;
        this.f$1 = str;
        this.f$2 = z;
    }

    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
        this.f$0.m848xe00ce193(this.f$1, this.f$2, tLObject, tL_error);
    }
}
